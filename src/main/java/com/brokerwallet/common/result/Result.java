package com.brokerwallet.common.result;

import lombok.Data;

@Data
public class Result<T> {

    private int code;       // 0 表示成功，非0表示错误码
    private String message; // 提示信息
    private T data;         // 成功返回的数据

    // 成功
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }

    // 成功，可自定义消息
    public static <T> Result<T> ok(T data, String msg) {
        Result<T> r = new Result<>();
        r.code = 0;
        r.message = msg;
        r.data = data;
        return r;
    }

    // 失败
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = msg;
        r.data = null;
        return r;
    }

    // 失败，默认业务码 1
    public static <T> Result<T> fail(String msg) {
        return fail(1, msg);
    }

}
