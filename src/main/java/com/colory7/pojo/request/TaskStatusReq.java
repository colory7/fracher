package com.colory7.pojo.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskStatusReq {

    @JsonProperty(value = "task_ids")
    private String taskIds;

    public String getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskStatus{");
        sb.append("taskIds='").append(taskIds).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
