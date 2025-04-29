package com.example.customer.domain.model;

import com.example.common.core.domain.ddd.Entity;
import lombok.Getter;

/**
 * 联系人实体
 */
@Getter
public class Contact implements Entity<String> {
    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID
     */
    private String contactId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 是否主要联系人
     */
    private Boolean isPrimary;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 所属客户ID
     */
    private String customerId;

    /**
     * 无参构造函数（供JPA使用）
     */
    protected Contact() {
    }

    /**
     * 构造函数
     */
    public Contact(String contactId, String name, String phone, String email, String position, 
                   String department, Boolean isPrimary, String remark, String customerId) {
        this.contactId = contactId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.department = department;
        this.isPrimary = isPrimary != null && isPrimary;
        this.remark = remark;
        this.customerId = customerId;
    }

    /**
     * 更新联系人信息
     */
    public void updateContact(String name, String phone, String email, String position, 
                              String department, String remark) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.department = department;
        this.remark = remark;
    }

    /**
     * 设置为主要联系人
     */
    public void setAsPrimary() {
        this.isPrimary = true;
    }

    /**
     * 取消主要联系人
     */
    public void cancelPrimary() {
        this.isPrimary = false;
    }

    @Override
    public String getId() {
        return this.contactId;
    }
}
