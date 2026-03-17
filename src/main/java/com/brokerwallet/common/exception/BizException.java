package com.brokerwallet.common.exception;


import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final int code; //业务码

    public BizException(String message) {
        super(message);
        this.code = 400;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

}
