package com.colory7.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskCreate {
    @JsonProperty(value = "task_id")
    private String taskId;

    @JsonProperty(value = "url_type")
    private String urlType;

    private String url;

    private String roi;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }
}
