package com.example.customer.domain.event;

import com.example.common.core.domain.ddd.AbstractDomainEvent;
import lombok.Getter;

/**
 * 客户级别变更事件
 */
@Getter
public class CustomerLevelChangedEvent extends AbstractDomainEvent {
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
     * 旧级别代码
     */
    private final String oldLevelCode;
    
    /**
     * 新级别代码
     */
    private final String newLevelCode;

    /**
     * 构造函数
     */
    public CustomerLevelChangedEvent(String customerId, String customerName, String oldLevelCode, String newLevelCode) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.oldLevelCode = oldLevelCode;
        this.newLevelCode = newLevelCode;
    }
}
