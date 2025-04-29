package com.example.common.core.exception;

import com.example.common.core.domain.IResultCode;
import com.example.common.core.domain.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(String message) {
        this.code = ResultCode.FAILED.getCode();
        this.message = message;
    }

    public BusinessException(IResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(IResultCode resultCode, String message) {
        this.code = resultCode.getCode();
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
