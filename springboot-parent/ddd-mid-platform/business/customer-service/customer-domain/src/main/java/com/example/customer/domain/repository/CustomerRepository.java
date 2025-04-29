package com.example.customer.domain.repository;

import com.example.common.core.domain.PageQuery;
import com.example.common.core.domain.ddd.Repository;
import com.example.customer.domain.model.Customer;

import java.util.List;

/**
 * 客户仓储接口
 */
public interface CustomerRepository extends Repository<Customer, String> {
    
    /**
     * 根据客户名称查找客户
     *
     * @param customerName 客户名称
     * @return 客户列表
     */
    List<Customer> findByCustomerName(String customerName);
    
    /**
     * 根据级别代码查找客户
     *
     * @param levelCode 级别代码
     * @return 客户列表
     */
    List<Customer> findByLevelCode(String levelCode);
    
    /**
     * 根据电话号码查找客户
     *
     * @param phone 电话号码
     * @return 客户列表
     */
    List<Customer> findByPhone(String phone);
    
    /**
     * 根据邮箱地址查找客户
     *
     * @param email 邮箱地址
     * @return 客户列表
     */
    List<Customer> findByEmail(String email);
    
    /**
     * 分页查询客户列表
     *
     * @param pageQuery 分页查询参数
     * @return 客户列表
     */
    List<Customer> findByPage(PageQuery pageQuery);
    
    /**
     * 计算客户总数
     *
     * @return 客户总数
     */
    long countAll();
}
