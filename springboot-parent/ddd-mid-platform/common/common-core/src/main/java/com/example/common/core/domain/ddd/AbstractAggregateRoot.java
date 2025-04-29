package com.example.common.core.domain.ddd;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DDD - 聚合根抽象实现
 *
 * @param <ID> ID类型
 */
public abstract class AbstractAggregateRoot<ID extends Serializable> implements AggregateRoot<ID> {
    private static final long serialVersionUID = 1L;

    /**
     * 领域事件列表
     */
    @Getter(lombok.AccessLevel.PROTECTED)
    private transient final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 版本号（用于乐观锁）
     */
    @Getter
    @Setter
    private Long version;
    
    /**
     * 创建时间
     */
    @Getter
    @Setter
    private LocalDateTime createdTime;
    
    /**
     * 最后修改时间
     */
    @Getter
    @Setter
    private LocalDateTime lastModifiedTime;

    /**
     * 注册领域事件
     *
     * @param event 领域事件
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * 清除领域事件
     */
    public void clearEvents() {
        this.domainEvents.clear();
    }

    /**
     * 获取领域事件列表（不可修改）
     *
     * @return 领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 发布所有注册的领域事件
     */
    public void publishEvents() {
        domainEvents.forEach(DomainEventPublisher::publish);
        clearEvents();
    }
}
