package com.example.common.core.domain;

/**
 * 返回码接口
 */
public interface IResultCode {
    /**
     * 获取返回码
     *
     * @return 返回码
     */
    Integer getCode();

    /**
     * 获取返回消息
     *
     * @return 返回消息
     */
    String getMessage();
}
