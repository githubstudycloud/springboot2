package com.example.common.core.domain.ddd;

import java.io.Serializable;

/**
 * DDD - 值对象接口
 */
public interface ValueObject extends Serializable {

    /**
     * 判断值对象是否相等
     * 值对象通过其所有属性值判断相等性，而不是通过ID
     *
     * @param other 其他值对象
     * @return 是否相等
     */
    boolean sameValueAs(ValueObject other);
}
