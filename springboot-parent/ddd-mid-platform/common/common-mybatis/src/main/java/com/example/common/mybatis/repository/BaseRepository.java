package com.example.common.mybatis.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.core.domain.ddd.AggregateRoot;
import com.example.common.core.domain.ddd.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * DDD - 仓储接口的基础实现
 *
 * @param <T>  聚合根类型
 * @param <E>  实体类型（用于持久化的DO对象）
 * @param <ID> ID类型
 */
public abstract class BaseRepository<T extends AggregateRoot<ID>, E, ID extends Serializable>
        implements Repository<T, ID> {

    /**
     * 获取Mapper对象
     *
     * @return Mapper对象
     */
    protected abstract BaseMapper<E> getMapper();

    /**
     * 实体转聚合根
     *
     * @param entity 实体
     * @return 聚合根
     */
    protected abstract T toAggregate(E entity);

    /**
     * 聚合根转实体
     *
     * @param aggregate 聚合根
     * @return 实体
     */
    protected abstract E toEntity(T aggregate);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T save(T aggregate) {
        // 发布领域事件
        aggregate.publishEvents();
        
        // 转换为实体并保存
        E entity = toEntity(aggregate);
        if (aggregate.getId() != null && this.existsById(aggregate.getId())) {
            getMapper().updateById(entity);
        } else {
            getMapper().insert(entity);
        }
        
        // 返回保存后的聚合根
        return toAggregate(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        E entity = getMapper().selectById(id);
        return Optional.ofNullable(entity).map(this::toAggregate);
    }

    @Override
    public boolean existsById(ID id) {
        return getMapper().selectById(id) != null;
    }

    @Override
    public List<T> findAll() {
        List<E> entities = getMapper().selectList(Wrappers.emptyWrapper());
        return entities.stream().map(this::toAggregate).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(ID id) {
        getMapper().deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(T aggregate) {
        deleteById(aggregate.getId());
    }

    @Override
    public long count() {
        return getMapper().selectCount(Wrappers.emptyWrapper());
    }
    
    /**
     * 创建Lambda查询包装器
     *
     * @return Lambda查询包装器
     */
    protected LambdaQueryWrapper<E> lambdaQuery() {
        return Wrappers.lambdaQuery();
    }
}
