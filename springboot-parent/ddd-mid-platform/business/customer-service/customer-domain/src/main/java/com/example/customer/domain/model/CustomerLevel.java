package com.example.customer.domain.model;

import com.example.common.core.domain.ddd.ValueObject;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 客户级别值对象
 */
@Getter
public class CustomerLevel implements ValueObject {
    private static final long serialVersionUID = 1L;

    /**
     * 级别代码
     */
    private final String levelCode;

    /**
     * 级别名称
     */
    private final String levelName;

    /**
     * 优惠比例
     */
    private final BigDecimal discountRate;

    /**
     * 构造函数
     */
    public CustomerLevel(String levelCode, String levelName, BigDecimal discountRate) {
        this.levelCode = levelCode;
        this.levelName = levelName;
        this.discountRate = discountRate;
    }

    /**
     * 判断值对象是否相等
     */
    @Override
    public boolean sameValueAs(ValueObject other) {
        if (!(other instanceof CustomerLevel)) {
            return false;
        }
        CustomerLevel that = (CustomerLevel) other;
        return this.levelCode.equals(that.levelCode) &&
                this.levelName.equals(that.levelName) &&
                this.discountRate.compareTo(that.discountRate) == 0;
    }
    
    /**
     * 获取常规客户级别
     */
    public static CustomerLevel regularLevel() {
        return new CustomerLevel("REGULAR", "普通客户", new BigDecimal("1.0"));
    }
    
    /**
     * 获取VIP客户级别
     */
    public static CustomerLevel vipLevel() {
        return new CustomerLevel("VIP", "VIP客户", new BigDecimal("0.9"));
    }
    
    /**
     * 获取高级VIP客户级别
     */
    public static CustomerLevel vipPlusLevel() {
        return new CustomerLevel("VIP_PLUS", "高级VIP客户", new BigDecimal("0.8"));
    }
}
