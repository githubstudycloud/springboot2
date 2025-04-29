package com.example.customer.domain.model;

import com.example.common.core.domain.ddd.Entity;
import lombok.Getter;

/**
 * 地址实体
 */
@Getter
public class Address implements Entity<String> {
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
    
    /**
     * 所属客户ID
     */
    private String customerId;

    /**
     * 无参构造函数（供JPA使用）
     */
    protected Address() {
    }

    /**
     * 构造函数
     */
    public Address(String addressId, String addressType, String country, String province, 
                  String city, String district, String detailAddress, String zipCode, 
                  Boolean isDefault, String customerId) {
        this.addressId = addressId;
        this.addressType = addressType;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.isDefault = isDefault != null && isDefault;
        this.customerId = customerId;
    }

    /**
     * 更新地址信息
     */
    public void updateAddress(String addressType, String country, String province, 
                             String city, String district, String detailAddress, 
                             String zipCode) {
        this.addressType = addressType;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

    /**
     * 设置为默认地址
     */
    public void setAsDefault() {
        this.isDefault = true;
    }

    /**
     * 取消默认地址
     */
    public void cancelDefault() {
        this.isDefault = false;
    }

    @Override
    public String getId() {
        return this.addressId;
    }
}
