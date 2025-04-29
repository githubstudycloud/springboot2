package com.example.common.core.domain.ddd;

import java.io.Serializable;

/**
 * DDD - 聚合根接口
 * 聚合根是一种特殊的实体，它是一个聚合的入口点
 *
 * @param <ID> ID类型
 */
public interface AggregateRoot<ID extends Serializable> extends Entity<ID> {
    
    /**
     * 获取聚合根版本（用于乐观锁）
     *
     * @return 版本号
     */
    Long getVersion();
}
