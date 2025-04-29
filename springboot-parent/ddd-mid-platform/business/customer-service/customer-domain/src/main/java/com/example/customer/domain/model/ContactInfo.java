package com.example.customer.domain.model;

import com.example.common.core.domain.ddd.ValueObject;
import lombok.Getter;

/**
 * 联系方式值对象
 */
@Getter
public class ContactInfo implements ValueObject {
    private static final long serialVersionUID = 1L;

    /**
     * 电话号码
     */
    private final String phone;

    /**
     * 邮箱地址
     */
    private final String email;

    /**
     * 构造函数
     */
    public ContactInfo(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    /**
     * 判断值对象是否相等
     */
    @Override
    public boolean sameValueAs(ValueObject other) {
        if (!(other instanceof ContactInfo)) {
            return false;
        }
        ContactInfo that = (ContactInfo) other;
        
        // 比较电话和邮箱，允许为null
        return (this.phone == null ? that.phone == null : this.phone.equals(that.phone)) &&
                (this.email == null ? that.email == null : this.email.equals(that.email));
    }
    
    /**
     * 是否有效的联系方式（至少有电话或邮箱）
     */
    public boolean isValid() {
        return (phone != null && !phone.isEmpty()) || (email != null && !email.isEmpty());
    }
}
