package com.example.customer.domain.event;

import com.example.common.core.domain.ddd.AbstractDomainEvent;
import lombok.Getter;

/**
 * 客户信息更新事件
 */
@Getter
public class CustomerUpdatedEvent extends AbstractDomainEvent {
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
     * 更新类型（BASIC-基本信息更新，CONTACT-联系方式更新）
     */
    private final String updateType;

    /**
     * 构造函数
     */
    public CustomerUpdatedEvent(String customerId, String customerName, String updateType) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.updateType = updateType;
    }
    
    /**
     * 基本信息更新
     */
    public static CustomerUpdatedEvent basicInfoUpdated(String customerId, String customerName) {
        return new CustomerUpdatedEvent(customerId, customerName, "BASIC");
    }
    
    /**
     * 联系方式更新
     */
    public static CustomerUpdatedEvent contactInfoUpdated(String customerId, String customerName) {
        return new CustomerUpdatedEvent(customerId, customerName, "CONTACT");
    }
}
