package com.example.customer.app.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.customer.app.repository.entity.CustomerDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户Mapper接口
 */
@Mapper
public interface CustomerMapper extends BaseMapper<CustomerDO> {

    /**
     * 根据电话号码查询客户
     *
     * @param phone 电话号码
     * @return 客户列表
     */
    List<CustomerDO> selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱地址查询客户
     *
     * @param email 邮箱地址
     * @return 客户列表
     */
    List<CustomerDO> selectByEmail(@Param("email") String email);

    /**
     * 根据客户级别查询客户
     *
     * @param levelCode 级别代码
     * @return 客户列表
     */
    List<CustomerDO> selectByLevelCode(@Param("levelCode") String levelCode);
}
