package com.example.customer.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 联系人DTO
 */
@Data
public class ContactDTO implements Serializable {
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
}
