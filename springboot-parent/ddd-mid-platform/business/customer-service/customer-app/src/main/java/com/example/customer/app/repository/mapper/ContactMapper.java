package com.example.customer.app.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.customer.app.repository.entity.ContactDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 联系人Mapper接口
 */
@Mapper
public interface ContactMapper extends BaseMapper<ContactDO> {

    /**
     * 根据客户ID查询联系人列表
     *
     * @param customerId 客户ID
     * @return 联系人列表
     */
    List<ContactDO> selectByCustomerId(@Param("customerId") String customerId);

    /**
     * 根据客户ID删除联系人
     *
     * @param customerId 客户ID
     * @return 影响行数
     */
    int deleteByCustomerId(@Param("customerId") String customerId);
}
