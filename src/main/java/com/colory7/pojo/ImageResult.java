package com.colory7.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ImageResult {
    @JsonProperty(value = "task_id")
    private String taskId;
    @JsonProperty(value = "person_num")
    private Integer personNum;
    private Integer in;
    private Integer out;
    @JsonProperty(value = "image_base64")
    private String imageBase64;

    private List<Row> data;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getPersonNum() {
        return personNum;
    }

    public void setPersonNum(Integer personNum) {
        this.personNum = personNum;
    }

    public Integer getIn() {
        return in;
    }

    public void setIn(Integer in) {
        this.in = in;
    }

    public Integer getOut() {
        return out;
    }

    public void setOut(Integer out) {
        this.out = out;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public List<Row> getData() {
        return data;
    }

    public void setData(List<Row> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImageResult{");
        sb.append("taskId='").append(taskId).append('\'');
        sb.append(", personNum=").append(personNum);
        sb.append(", in=").append(in);
        sb.append(", out=").append(out);
        sb.append(", imageBase64='").append(imageBase64).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
