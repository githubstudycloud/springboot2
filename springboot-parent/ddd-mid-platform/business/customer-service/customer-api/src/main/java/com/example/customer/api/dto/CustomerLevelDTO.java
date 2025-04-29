package com.example.customer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户级别DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLevelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 级别代码
     */
    private String levelCode;

    /**
     * 级别名称
     */
    private String levelName;

    /**
     * 优惠比例
     */
    private BigDecimal discountRate;
}
