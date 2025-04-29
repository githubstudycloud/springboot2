package com.example.customer.app.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地址数据对象
 */
@Data
@TableName("t_customer_address")
public class AddressDO {

    /**
     * 地址ID
     */
    @TableId(value = "address_id", type = IdType.INPUT)
    private String addressId;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private String customerId;

    /**
     * 地址类型（HOME-家庭地址，WORK-工作地址，OTHER-其他地址）
     */
    @TableField("address_type")
    private String addressType;
    
    /**
     * 国家
     */
    @TableField("country")
    private String country;
    
    /**
     * 省份
     */
    @TableField("province")
    private String province;
    
    /**
     * 城市
     */
    @TableField("city")
    private String city;
    
    /**
     * 区/县
     */
    @TableField("district")
    private String district;
    
    /**
     * 详细地址
     */
    @TableField("detail_address")
    private String detailAddress;
    
    /**
     * 邮政编码
     */
    @TableField("zip_code")
    private String zipCode;
    
    /**
     * 是否默认地址
     */
    @TableField("is_default")
    private Boolean isDefault;
    
    /**
     * 是否删除
     */
    @TableField("deleted")
    private Boolean deleted;
}
