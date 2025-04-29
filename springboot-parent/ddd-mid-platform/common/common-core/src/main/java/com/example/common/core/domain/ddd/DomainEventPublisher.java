package com.example.common.core.domain.ddd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * DDD - 领域事件发布器
 */
@Slf4j
@Component
public class DomainEventPublisher implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        DomainEventPublisher.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 发布领域事件
     *
     * @param event 领域事件
     */
    public static void publish(DomainEvent event) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new DomainEventWrapper(event));
            log.info("领域事件已发布: {}", event.getEventType());
        } else {
            log.warn("ApplicationEventPublisher未初始化，领域事件未发布: {}", event.getEventType());
        }
    }

    /**
     * 领域事件包装器
     * 将领域事件包装为Spring的ApplicationEvent
     */
    public static class DomainEventWrapper extends ApplicationEvent {
        private static final long serialVersionUID = 1L;

        public DomainEventWrapper(DomainEvent event) {
            super(event);
        }

        public DomainEvent getDomainEvent() {
            return (DomainEvent) getSource();
        }
    }
}
