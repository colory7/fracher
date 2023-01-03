package com.colory7.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("响应实体")
public class Result<T> {

    @ApiModelProperty(value = "结果编码",allowableValues = "",notes = "0表示成功,99表示失败")
    @JsonProperty(value = "result_code")
    private Integer resultCode;

    @ApiModelProperty("结果描述")
    private String msg;


    public Result() {
        super();
    }

    public Result(Integer resultCode, String msg, T data) {
        this.resultCode = resultCode;
        this.msg = msg;
    }

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


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Result{");
        sb.append("resultCode=").append(resultCode);
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
