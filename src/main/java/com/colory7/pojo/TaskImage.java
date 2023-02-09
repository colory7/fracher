package com.colory7.pojo;

public class TaskImage {

    private String taskId;
    private String roi;
    private String imageUrl;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskImage{");
        sb.append("taskId='").append(taskId).append('\'');
        sb.append(", roi='").append(roi).append('\'');
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
