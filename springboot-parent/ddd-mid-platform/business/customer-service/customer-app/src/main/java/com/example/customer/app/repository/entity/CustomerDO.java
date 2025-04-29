package com.example.customer.app.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户数据对象
 */
@Data
@TableName(value = "t_customer", autoResultMap = true)
public class CustomerDO {

    /**
     * 客户ID
     */
    @TableId(value = "customer_id", type = IdType.INPUT)
    private String customerId;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 电话号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 级别代码
     */
    @TableField("level_code")
    private String levelCode;

    /**
     * 级别名称
     */
    @TableField("level_name")
    private String levelName;

    /**
     * 折扣率
     */
    @TableField("discount_rate")
    private String discountRate;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Boolean deleted;
    
    /**
     * 版本号
     */
    @TableField("version")
    private Long version;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 最后修改时间
     */
    @TableField("last_modified_time")
    private LocalDateTime lastModifiedTime;
}
