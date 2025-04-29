package com.example.common.core.domain;

/**
 * 常用返回码枚举
 */
public enum ResultCode implements IResultCode {
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 操作失败
     */
    FAILED(500, "操作失败"),
    
    /**
     * 参数校验失败
     */
    VALIDATE_FAILED(400, "参数检验失败"),
    
    /**
     * 未登录
     */
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    
    /**
     * 无权限
     */
    FORBIDDEN(403, "没有相关权限"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
