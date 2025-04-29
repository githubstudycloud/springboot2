package com.example.customer.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址DTO
 */
@Data
public class AddressDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    private String addressId;

    /**
     * 地址类型（HOME-家庭地址，WORK-工作地址，OTHER-其他地址）
     */
    private String addressType;
    
    /**
     * 国家
     */
    private String country;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区/县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 邮政编码
     */
    private String zipCode;
    
    /**
     * 是否默认地址
     */
    private Boolean isDefault;
}
