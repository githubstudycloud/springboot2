package com.example.common.core.domain.ddd;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DDD - 领域事件抽象实现
 */
@Getter
public abstract class AbstractDomainEvent implements DomainEvent {
    private static final long serialVersionUID = 1L;

    /**
     * 事件发生时间
     */
    private final LocalDateTime occurredOn;

    /**
     * 事件唯一标识
     */
    private final String eventId;

    protected AbstractDomainEvent() {
        this.occurredOn = LocalDateTime.now();
        this.eventId = java.util.UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public String toString() {
        return "DomainEvent{" +
                "eventType='" + getEventType() + '\'' +
                ", eventId='" + eventId + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
