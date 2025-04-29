package com.example.common.core.domain.ddd;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DDD - 领域事件接口
 */
public interface DomainEvent extends Serializable {

    /**
     * 获取事件发生时间
     *
     * @return 发生时间
     */
    LocalDateTime occurredOn();
    
    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}
