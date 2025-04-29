package com.example.customer.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户DTO
 */
@Data
public class CustomerDTO implements Serializable {
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
    private CustomerLevelDTO customerLevel;

    /**
     * 联系方式
     */
    private ContactInfoDTO contactInfo;

    /**
     * 地址列表
     */
    private List<AddressDTO> addresses;

    /**
     * 联系人列表
     */
    private List<ContactDTO> contacts;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifiedTime;
}
