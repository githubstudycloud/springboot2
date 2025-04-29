package com.example.customer.domain.service;

import com.example.common.core.exception.BusinessException;
import com.example.customer.domain.model.Customer;
import com.example.customer.domain.model.CustomerLevel;
import com.example.customer.domain.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * 客户领域服务
 */
@Slf4j
@Service
public class CustomerDomainService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerDomainService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    /**
     * 升级客户级别
     *
     * @param customerId 客户ID
     * @param levelCode  级别代码
     */
    @Transactional
    public void upgradeCustomerLevel(String customerId, String levelCode) {
        // 获取客户
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("客户不存在: " + customerId));
        
        // 如果级别相同，则不处理
        if (customer.getCustomerLevel().getLevelCode().equals(levelCode)) {
            log.info("客户级别未变更: {}", customerId);
            return;
        }
        
        // 根据级别代码创建新级别
        CustomerLevel newLevel;
        switch (levelCode) {
            case "REGULAR":
                newLevel = CustomerLevel.regularLevel();
                break;
            case "VIP":
                newLevel = CustomerLevel.vipLevel();
                break;
            case "VIP_PLUS":
                newLevel = CustomerLevel.vipPlusLevel();
                break;
            default:
                throw new BusinessException("不支持的客户级别: " + levelCode);
        }
        
        // 变更客户级别
        customer.changeLevel(newLevel);
        
        // 保存客户
        customerRepository.save(customer);
        
        log.info("客户级别已升级: {}, 从 {} 升级到 {}", 
                customerId, 
                customer.getCustomerLevel().getLevelCode(), 
                levelCode);
    }
    
    /**
     * 检查客户名称是否已存在
     *
     * @param customerName 客户名称
     * @return 是否存在
     */
    public boolean isCustomerNameExists(String customerName) {
        List<Customer> customers = customerRepository.findByCustomerName(customerName);
        return !customers.isEmpty();
    }
    
    /**
     * 检查电话号码是否已存在
     *
     * @param phone 电话号码
     * @return 是否存在
     */
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        List<Customer> customers = customerRepository.findByPhone(phone);
        return !customers.isEmpty();
    }
    
    /**
     * 检查邮箱地址是否已存在
     *
     * @param email 邮箱地址
     * @return 是否存在
     */
    public boolean isEmailExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        List<Customer> customers = customerRepository.findByEmail(email);
        return !customers.isEmpty();
    }
}
