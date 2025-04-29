package com.example.customer.app.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 联系人数据对象
 */
@Data
@TableName("t_customer_contact")
public class ContactDO {

    /**
     * 联系人ID
     */
    @TableId(value = "contact_id", type = IdType.INPUT)
    private String contactId;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private String customerId;
    
    /**
     * 姓名
     */
    @TableField("name")
    private String name;
    
    /**
     * 电话
     */
    @TableField("phone")
    private String phone;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 职位
     */
    @TableField("position")
    private String position;
    
    /**
     * 部门
     */
    @TableField("department")
    private String department;
    
    /**
     * 是否主要联系人
     */
    @TableField("is_primary")
    private Boolean isPrimary;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 是否删除
     */
    @TableField("deleted")
    private Boolean deleted;
}
