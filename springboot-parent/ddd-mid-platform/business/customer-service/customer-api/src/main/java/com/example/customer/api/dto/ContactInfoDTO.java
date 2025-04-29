package com.example.customer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 联系方式DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;
}
