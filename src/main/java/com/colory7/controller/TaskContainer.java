package com.colory7.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.colory7.common.TaskStatusEnum;
import com.colory7.pojo.ImageResult;
import com.colory7.pojo.Task;
import org.bytedeco.javacv.Frame;

import java.util.LinkedList;
import java.util.concurrent.*;

public class TaskContainer {

    // 任务集合
    private static final ConcurrentHashMap<String, Task> TASKS = new ConcurrentHashMap(16);
    public static final ConcurrentHashMap<String, LinkedList<ImageResult>> TASK_IMAGE_RESULTS = new ConcurrentHashMap(8);
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<Frame>> FRAME_QUEUE = new ConcurrentHashMap(8);

    // 执行任务线程池
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("task-pool-%d").build();
    private static final ExecutorService TASK_POOL = new ThreadPoolExecutor(20, 80, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(80), THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());


    public static Task getTask(String taskId) {
        return TASKS.get(taskId);
    }

    public static Task createTask(String taskId, Task task) {
        return TASKS.put(taskId, task);
    }

    public static ConcurrentLinkedQueue<Frame> getFrameQueue(String taskId) {
        return FRAME_QUEUE.get(taskId);
    }

    public static ConcurrentLinkedQueue<Frame> initFrameQueue(String taskId) {
        return TaskContainer.FRAME_QUEUE.put(taskId, new ConcurrentLinkedQueue<>());
    }

    public static void submit(Runnable r) {
        TASK_POOL.submit(r);
    }

    public static void clear(String taskId) {
        TaskContainer.TASK_IMAGE_RESULTS.remove(taskId);
        TaskContainer.FRAME_QUEUE.remove(taskId);
        TaskContainer.TASKS.remove(taskId);
    }

//    public static void stop(String taskId) {
//        TaskContainer.TASKS.get(taskId).setTaskStatus(TaskStatusEnum.STOPED);
//    }


    public static boolean isContinue(String taskId) {
        return TaskContainer.TASKS.get(taskId).getTaskStatus().equals(TaskStatusEnum.CONTINUE);
    }

    public static void stopping(String taskId) {
        TaskContainer.TASKS.get(taskId).setTaskStatus(TaskStatusEnum.STOPPING);
    }

    public static boolean isHandleFrameStopped(String taskId) {
        return TaskContainer.TASKS.get(taskId).getTaskStatus().equals(TaskStatusEnum.HANDLE_FRAME_STOPPED);
    }

    public static void stopHandleFrame(String taskId) {
        TaskContainer.TASKS.get(taskId).setTaskStatus(TaskStatusEnum.HANDLE_FRAME_STOPPED);
    }

}
