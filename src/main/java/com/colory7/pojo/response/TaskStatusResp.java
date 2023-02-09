package com.colory7.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskStatusResp {

    @JsonProperty(value = "result_code")
    private Integer resultCode;
    private String msg;
    @JsonProperty(value = "delete_task_ids")
    private String deleteTaskIds;
    private String roi;
    private String url;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDeleteTaskIds() {
        return deleteTaskIds;
    }

    public void setDeleteTaskIds(String deleteTaskIds) {
        this.deleteTaskIds = deleteTaskIds;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskStatus{");
        sb.append("resultCode=").append(resultCode);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", deleteTaskIds='").append(deleteTaskIds).append('\'');
        sb.append(", roi='").append(roi).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
