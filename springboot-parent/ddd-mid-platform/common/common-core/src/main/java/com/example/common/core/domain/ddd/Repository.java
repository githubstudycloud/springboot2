package com.example.common.core.domain.ddd;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * DDD - 仓储接口
 *
 * @param <T>  聚合根类型
 * @param <ID> ID类型
 */
public interface Repository<T extends AggregateRoot<ID>, ID extends Serializable> {

    /**
     * 保存聚合根
     *
     * @param aggregateRoot 聚合根
     * @return 保存后的聚合根
     */
    T save(T aggregateRoot);

    /**
     * 通过ID查找聚合根
     *
     * @param id ID
     * @return 聚合根
     */
    Optional<T> findById(ID id);

    /**
     * 检查ID对应的聚合根是否存在
     *
     * @param id ID
     * @return 是否存在
     */
    boolean existsById(ID id);

    /**
     * 查找所有聚合根
     *
     * @return 聚合根列表
     */
    List<T> findAll();

    /**
     * 删除指定ID的聚合根
     *
     * @param id ID
     */
    void deleteById(ID id);

    /**
     * 删除指定的聚合根
     *
     * @param aggregateRoot 聚合根
     */
    void delete(T aggregateRoot);

    /**
     * 获取聚合根数量
     *
     * @return 数量
     */
    long count();
}
