package com.example.common.core.domain.ddd;

import java.io.Serializable;

/**
 * DDD - 实体接口
 *
 * @param <ID> ID类型
 */
public interface Entity<ID extends Serializable> {

    /**
     * 获取实体ID
     *
     * @return 实体ID
     */
    ID getId();

    /**
     * 判断实体是否相等
     *
     * @param other 其他实体
     * @return 是否相等
     */
    default boolean sameIdentityAs(Entity<ID> other) {
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        if (this.getId() == null || other.getId() == null) {
            return false;
        }
        return this.getId().equals(other.getId());
    }
}
