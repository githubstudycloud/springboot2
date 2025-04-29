package com.example.customer.domain.event;

import com.example.common.core.domain.ddd.AbstractDomainEvent;
import lombok.Getter;

/**
 * 客户创建事件
 */
@Getter
public class CustomerCreatedEvent extends AbstractDomainEvent {
    private static final long serialVersionUID = 1L;
    
    /**
     * 客户ID
     */
    private final String customerId;
    
    /**
     * 客户名称
     */
    private final String customerName;
    
    /**
     * 客户级别代码
     */
    private final String levelCode;

    /**
     * 构造函数
     */
    public CustomerCreatedEvent(String customerId, String customerName, String levelCode) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.levelCode = levelCode;
    }
}
