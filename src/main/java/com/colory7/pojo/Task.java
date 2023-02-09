package com.colory7.pojo;

import com.colory7.common.TaskStatusEnum;

public class Task {
    private String taskId;
    private volatile TaskStatusEnum taskStatus;
    private String createDate;
    private String roi;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }
}
