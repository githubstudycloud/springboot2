package com.example.customer.domain.model;

import com.example.common.core.domain.ddd.AbstractAggregateRoot;
import com.example.customer.domain.event.CustomerCreatedEvent;
import com.example.customer.domain.event.CustomerLevelChangedEvent;
import com.example.customer.domain.event.CustomerUpdatedEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 客户聚合根
 */
@Getter
public class Customer extends AbstractAggregateRoot<String> {
    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户级别
     */
    private CustomerLevel customerLevel;

    /**
     * 联系方式
     */
    private ContactInfo contactInfo;

    /**
     * 地址列表
     */
    private final List<Address> addresses = new ArrayList<>();

    /**
     * 联系人列表
     */
    private final List<Contact> contacts = new ArrayList<>();

    /**
     * 无参构造函数（供JPA使用）
     */
    protected Customer() {
    }

    /**
     * 创建新客户
     */
    public Customer(String customerName, CustomerLevel customerLevel, ContactInfo contactInfo) {
        // 生成新ID
        this.customerId = UUID.randomUUID().toString().replace("-", "");
        this.customerName = customerName;
        this.customerLevel = customerLevel;
        this.contactInfo = contactInfo;
        
        // 设置审计字段
        setCreatedTime(LocalDateTime.now());
        setLastModifiedTime(getCreatedTime());
        
        // 注册客户创建事件
        registerEvent(new CustomerCreatedEvent(customerId, customerName, customerLevel.getLevelCode()));
    }
    
    /**
     * 更新客户基本信息
     */
    public void updateBasicInfo(String customerName) {
        this.customerName = customerName;
        this.setLastModifiedTime(LocalDateTime.now());
        
        // 注册客户更新事件
        registerEvent(CustomerUpdatedEvent.basicInfoUpdated(customerId, customerName));
    }
    
    /**
     * 更新客户联系方式
     */
    public void updateContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
        this.setLastModifiedTime(LocalDateTime.now());
        
        // 注册客户联系方式更新事件
        registerEvent(CustomerUpdatedEvent.contactInfoUpdated(customerId, customerName));
    }
    
    /**
     * 更新客户级别
     */
    public void changeLevel(CustomerLevel newLevel) {
        String oldLevelCode = this.customerLevel.getLevelCode();
        this.customerLevel = newLevel;
        this.setLastModifiedTime(LocalDateTime.now());
        
        // 注册客户级别变更事件
        registerEvent(new CustomerLevelChangedEvent(customerId, customerName, oldLevelCode, newLevel.getLevelCode()));
    }
    
    /**
     * 添加地址
     */
    public void addAddress(Address address) {
        // 如果设置为默认地址，则取消其他地址的默认标志
        if (address.getIsDefault()) {
            addresses.forEach(a -> a.cancelDefault());
        }
        addresses.add(address);
        this.setLastModifiedTime(LocalDateTime.now());
    }
    
    /**
     * 更新地址
     */
    public void updateAddress(Address address) {
        // 如果设置为默认地址，则取消其他地址的默认标志
        if (address.getIsDefault()) {
            addresses.stream()
                    .filter(a -> !a.getId().equals(address.getId()))
                    .forEach(a -> a.cancelDefault());
        }
        
        // 替换原有地址
        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).getId().equals(address.getId())) {
                addresses.set(i, address);
                break;
            }
        }
        
        this.setLastModifiedTime(LocalDateTime.now());
    }
    
    /**
     * 删除地址
     */
    public void removeAddress(String addressId) {
        addresses.removeIf(address -> address.getId().equals(addressId));
        this.setLastModifiedTime(LocalDateTime.now());
    }
    
    /**
     * 添加联系人
     */
    public void addContact(Contact contact) {
        // 如果设置为主要联系人，则取消其他联系人的主要标志
        if (contact.getIsPrimary()) {
            contacts.forEach(c -> c.cancelPrimary());
        }
        contacts.add(contact);
        this.setLastModifiedTime(LocalDateTime.now());
    }
    
    /**
     * 更新联系人
     */
    public void updateContact(Contact contact) {
        // 如果设置为主要联系人，则取消其他联系人的主要标志
        if (contact.getIsPrimary()) {
            contacts.stream()
                    .filter(c -> !c.getId().equals(contact.getId()))
                    .forEach(c -> c.cancelPrimary());
        }
        
        // 替换原有联系人
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(contact.getId())) {
                contacts.set(i, contact);
                break;
            }
        }
        
        this.setLastModifiedTime(LocalDateTime.now());
    }
    
    /**
     * 删除联系人
     */
    public void removeContact(String contactId) {
        contacts.removeIf(contact -> contact.getId().equals(contactId));
        this.setLastModifiedTime(LocalDateTime.now());
    }
    
    /**
     * 获取地址列表（不可修改）
     */
    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }
    
    /**
     * 获取联系人列表（不可修改）
     */
    public List<Contact> getContacts() {
        return Collections.unmodifiableList(contacts);
    }

    @Override
    public String getId() {
        return this.customerId;
    }
}
