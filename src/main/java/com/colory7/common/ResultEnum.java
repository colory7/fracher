package com.colory7.common;

public enum ResultEnum {

    SUCCESS(0,"成功"),
    EXISTS(0,"已存在"),
    FAILED(99,"失败"),
    ;
    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}