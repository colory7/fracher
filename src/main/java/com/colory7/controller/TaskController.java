package com.colory7.controller;

import com.alibaba.fastjson2.JSON;
import com.colory7.common.Result;
import com.colory7.common.ResultUtil;
import com.colory7.common.TaskStatusEnum;
import com.colory7.dto.TaskCreate;
import com.colory7.pojo.Task;
import com.colory7.pojo.TaskImage;
import com.colory7.pojo.request.TaskStatusReq;
import com.colory7.pojo.response.TaskStatusResp;
import com.colory7.util.DateUtil;
import com.colory7.util.MinioUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;


@RestController
@RequestMapping("/ai-vision/dynamic")
@Api("任务管理类")
@Slf4j
public class TaskController {

    @Value("${task.image-queue}")
    private String taskImageQueue;

    @Value("${task.timeout}")
    private Long taskTimeout;

    @Value("${task.monitor-time}")
    private Long monitorTime;

    @Value("${task.monior-url}")
    private String moniorUrl;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MinioUtil minioUtil;

//    @GetMapping("/")
//    @ResponseBody
//    @ApiOperation("查询所有任务")
//    public ConcurrentHashMap root() {
//        return TaskContainer.TASKS;
//    }

    /**
     * 创建任务
     *
     * @param taskCreate
     * @return
     */
    @PostMapping(value = "/tasks", produces = "application/json", consumes = "application/json")
    @ResponseBody
    @ApiOperation("创建任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "task_id", value = "任务ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "url_type", value = "类型", required = true, dataType = "String"),
            @ApiImplicitParam(name = "url", value = "视频流", required = true, dataType = "String"),
            @ApiImplicitParam(name = "roi", value = "感兴趣区域", required = true, dataType = "String"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功", response = Result.class),
            @ApiResponse(code = 0, message = "已存在", response = Result.class),
            @ApiResponse(code = 99, message = "失败", response = Result.class)
    })
    public synchronized Result tasks(@RequestBody TaskCreate taskCreate) {

        // 存放任务
        String taskId = taskCreate.getTaskId();
        Task task = new Task();
        task.setTaskId(taskId);
        String today = DateUtil.today();
        task.setCreateDate(today);
        task.setRoi(taskCreate.getRoi());

        boolean exists = false;
        Task cachedTask = TaskContainer.getTask(taskId);
        // taskId相同的任务已经存在
        int createTaskLevel = 0;

        if (cachedTask == null) {
            task.setTaskStatus(TaskStatusEnum.CONTINUE);
            TaskContainer.createTask(taskId, task);
            TaskContainer.initFrameQueue(taskId);

            createTaskLevel = 1;
            // taskId相同的任务不存在
        } else {
            exists = true;

            TaskContainer.stopping(taskId);

            //等待10次，每次等待1000ms

            for (int count = 0; count < 10; count++) {
                Task beStoppedTask = TaskContainer.getTask(taskId);
                //上一个相同任务id的任务已经停止
                if (beStoppedTask == null) {
                    task.setTaskStatus(TaskStatusEnum.CONTINUE);
                    TaskContainer.createTask(taskId, task);
                    TaskContainer.initFrameQueue(taskId);

                    createTaskLevel = 2;
                    break;
                    //上一个相同任务id的任务没有停止
                } else {
                    log.info("will sleep 1000 ms");
                    DateUtil.sleep(1000);
                }
            }
        }

        switch (createTaskLevel) {
            case 0:
                log.info("task exists");
                break;
            case 1:
                log.info("task will run");
            case 2:
                log.info("stop task and re-run");
                TaskContainer.submit(() ->
                        extractImageFromVideo(task, taskCreate.getUrl(), taskCreate.getRoi()));

                TaskContainer.submit(() ->
                        handleFrame(taskId));
                break;
        }

        if (exists) {
            return ResultUtil.exists();
        } else {
            return ResultUtil.success();
        }
    }


    /**
     * 抽取图片
     */
    public void extractImageFromVideo(Task task, String url, String roi) {
        String taskId = task.getTaskId();

        //抓取资源
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(url);
        // rtsp使用tcp协议传输数据
        frameGrabber.setOption("rtsp_transport", "tcp");

        try {
            // 开始抓取rtsp数据
            frameGrabber.start();
        } catch (Exception e) {
            // FIXME 异常处理
            log.error("extractImageFromVideo() error: " + e.getMessage(), e);
        }

        ConcurrentLinkedQueue<Frame> frameQueue = TaskContainer.getFrameQueue(taskId);
        Frame frame = null;

        long interval = 0;
        while (true) {
            try {
                // 判断任务是否被要求中断
                if (!task.getTaskStatus().equals(TaskStatusEnum.CONTINUE)) {
                    break;
                }

                frame = frameGrabber.grabImage();

                if (frame != null) {
                    // 每隔一段时间存储一帧
                    if (frame.timestamp / 250_000 >= interval) {
                        log.info("sample :" + interval);
                        frameQueue.offer(frame.clone());

                        interval++;
                    } else {
                        frame.close();
                    }
                }

            } catch (FFmpegFrameGrabber.Exception e) {
                log.error(e.getMessage(), e);
            }

        }

        try {
            // 停止抓取数据
            frameGrabber.stop();
            // 释放抓取资源
            frameGrabber.release();

            // 等待消费线程停止，再删除任务数据
            for (int i = 0; i < 60; i++) {
                if (!TaskContainer.isHandleFrameStopped(taskId)) {
                    DateUtil.sleep(1000);
                } else {
                    break;
                }
            }

            // 任务相关数据清空，则表示任务停止
            TaskContainer.clear(taskId);
        } catch (Exception e) {
            // FIXME 异常处理
            log.error("extractImageFromVideo() error: " + e.getMessage(), e);
        }

    }

    public void handleFrame(String taskId) {
        Frame frame = null;
        ConcurrentLinkedQueue<Frame> frames = TaskContainer.getFrameQueue(taskId);
        Task task = TaskContainer.getTask(taskId);
        // 记录上一次抓取图片不为空的时间戳
        long lastNonNullFrameTime = System.currentTimeMillis();

        // 图片编号
        long interval = 1;
        while (true) {
            // 判断任务是否被要求中断
            if (!task.getTaskStatus().equals(TaskStatusEnum.CONTINUE)) {
                break;
            }

            //消费张一图片数据
            frame = frames.poll();

            try {
                long current = System.currentTimeMillis();

                // 长时间抓取图片都为空，则停止抓取
                if (frame == null) {
//                    log.debug("taskId: " + taskId + " grab frame is null");

                    // 抓图片超时 比较当前时间和任务的处理时间差
                    if (current - lastNonNullFrameTime >= taskTimeout) {
                        TaskContainer.stopping(taskId);

                        log.warn("The task " + taskId + " is stopped because the frame that has been grabbed is null and the timeout is " + taskTimeout + " ms");
                        break;
                    }

                    // 睡眠250毫秒
                    DateUtil.sleep(250);
                    continue;

                } else {
//                    log.debug("taskId: " + taskId + " grab frame is not null");
                    // 更新上一次获取不为空的图片的时间戳
                    lastNonNullFrameTime = current;
                }

                // 达到一定时间间隔，固定发送请求，确认任务是否需要停止
                if (interval %30 == 29) {
                    try {
                        monitorTask(taskId);
                    } catch (Exception e) {
                        log.error("extractImageFromVideo() error: " + e.getMessage(), e);
                    }
                }

                // 保存图片到minio
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(frame);

                ByteArrayOutputStream os = new ByteArrayOutputStream(256 * 1024);
                ImageIO.write(bi, "jpg", os);
                ByteArrayInputStream in = new ByteArrayInputStream(os.toByteArray());

                String filePath = task.getCreateDate() + "_" + taskId + "_" + interval + ".jpg";
                String imageUrl = minioUtil.saveImageToMinio(filePath, in);

                // 任务的图片信息存储到redis队列
                TaskImage taskImage = new TaskImage();
                taskImage.setTaskId(taskId);
                taskImage.setRoi(task.getRoi());
                taskImage.setImageUrl(imageUrl);

                String json = JSON.toJSONString(taskImage);
                //FIXME 删除调试
                log.info(json);
                redisTemplate.opsForList().rightPush(taskImageQueue, json);

                interval++;
            } catch (Exception e) {
                // FIXME 异常处理
                log.error("extractImageFromVideo() error: " + e.getMessage(), e);
            } finally {
                if (frame != null) {
                    frame.close();
                }
            }

        }

        TaskContainer.stopHandleFrame(taskId);

    }

    /**
     * 定时监控任务状态,根据条件判断是否停止任务
     */
    public void monitorTask(String taskIds) {

        TaskStatusReq taskStatusReq = new TaskStatusReq();
        taskStatusReq.setTaskIds(taskIds);

        Mono<TaskStatusResp> resp = WebClient.create(moniorUrl)
                .patch()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(taskStatusReq)
                .retrieve()
                .bodyToMono(TaskStatusResp.class);


        // 订阅结果
        resp.timeout(Duration.ofMillis(monitorTime)).subscribe(response -> {
            // FIXME 日志级别
            log.info("monitorTask() ok: \n" + response);

            Integer resultCode = response.getResultCode();
            switch (resultCode) {
                //任务删除
                case -1:
                    String[] taskIdGroup = response.getDeleteTaskIds().split(",");
                    for (String taskId : taskIdGroup) {
                        TaskContainer.stopping(taskId);
                    }
                    break;

                //接收成功
                case 0:
                    break;

                //任务更新
                case 1:
                    break;
                default:
                    log.error("monitorTask() error result code: " + resultCode);
            }
        }, e -> {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                sb.append(stackTraceElement);
                sb.append("\n");
            }
            log.error("monitorTask() error: \n" + sb.toString());
        });

    }

}
