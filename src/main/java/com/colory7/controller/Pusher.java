package com.colory7.controller;

import com.alibaba.fastjson2.JSON;
import com.colory7.pojo.ImageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedList;


@Component
@Slf4j
@Order(Integer.MAX_VALUE)
public class Pusher {

    @Value("${task.image-result-queue}")
    private String imageResultQueue;

    @Value("${task.push-url}")
    private String pushUrl;

    @Value("${task.monitor-time}")
    private Long monitorTime;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取图片处理结果，并根据处理结果比较上一次处理结果进行推送数据
     */
    public void pullImageResultAndPush() {
        String imageResultJson = redisTemplate.opsForList().leftPop(imageResultQueue);

        if (imageResultJson != null) {
            // 最新的图片处理结果
            ImageResult latestImageResult = null;
            try {
                latestImageResult = JSON.parseObject(imageResultJson, ImageResult.class);
            } catch (Exception e) {
                log.error("pullImageResultAndPush() error: " + e.getMessage(), e);
                return;
            }

            // FIXME 日志级别
            log.info("latestImageResult: " + latestImageResult.toString());

            String taskId = latestImageResult.getTaskId();
            if (taskId == null) {
                log.error("pullImageResultAndPush() - taskId is null");
                return;
            }

            LinkedList<ImageResult> imageResults = TaskContainer.TASK_IMAGE_RESULTS.get(taskId);

            // 如果不同则推送数据，调用 动态人流数据推送接口
            if (imageResults == null) {
                LinkedList<ImageResult> initList = new LinkedList<>();
                initList.add(latestImageResult);

                TaskContainer.TASK_IMAGE_RESULTS.put(taskId, initList);
                pushImageResult(taskId, latestImageResult);
            } else {
                // 上一次的图片处理结果
                ImageResult lastTimeImageResult = null;
                if (imageResults.size() == 1) {
                    lastTimeImageResult = imageResults.get(0);
                } else {
                    lastTimeImageResult = imageResults.remove();
                }

                // 不同，推送数据
                // 相同，不推送数据
                if (latestImageResult.getPersonNum().equals(lastTimeImageResult.getPersonNum()) ||
                        latestImageResult.getIn().equals(lastTimeImageResult.getIn()) ||
                        latestImageResult.getOut().equals(lastTimeImageResult.getOut())
                ) {
                    pushImageResult(taskId, latestImageResult);
                } else {
                    imageResults.add(latestImageResult);
                }
            }
        }
    }


    /**
     * 将图片结果推送出去
     *
     * @param taskId
     * @param imageResult
     */
    private void pushImageResult(String taskId, ImageResult imageResult) {
        Mono<String> resp = WebClient.create(pushUrl + "/" + taskId)
                .patch()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(imageResult)
                .retrieve()
                .bodyToMono(String.class);

        resp.timeout(Duration.ofMillis(monitorTime)).subscribe(response -> {
            // FIXME 日志级别
            log.info("pushImageResult() ok: \n" + response);
        }, e -> {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                sb.append(stackTraceElement);
                sb.append("\n");
            }
            log.error("pushImageResult() error: \n" + sb.toString());
        });

    }

}
