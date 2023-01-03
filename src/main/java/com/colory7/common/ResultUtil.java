package com.colory7.common;

public class ResultUtil {

    private static final Result successResult;
    private static final Result failedResult;
    private static final Result existsResult;

    static {
        successResult = new Result();
        successResult.setResultCode(ResultEnum.SUCCESS.getCode());
        successResult.setMsg(ResultEnum.SUCCESS.getMsg());

        failedResult = new Result();
        failedResult.setResultCode(ResultEnum.FAILED.getCode());
        failedResult.setMsg(ResultEnum.FAILED.getMsg());

        existsResult = new Result();
        existsResult.setResultCode(ResultEnum.EXISTS.getCode());
        existsResult.setMsg(ResultEnum.EXISTS.getMsg());
    }

    /**
     * 成功
     **/
    public static Result success() {
       return successResult;
    }

    /**
     * 已存在
     **/
    public static Result exists() {
        return existsResult;
    }

    /**
     * 失败
     **/
    public static Result fail(Integer code, String msg) {
        return failedResult;
    }
}
