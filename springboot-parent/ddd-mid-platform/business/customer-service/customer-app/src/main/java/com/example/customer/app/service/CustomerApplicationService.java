package com.example.customer.app.service;

import com.example.common.core.domain.PageQuery;
import com.example.common.core.exception.BusinessException;
import com.example.customer.api.command.CreateCustomerCommand;
import com.example.customer.api.command.UpdateCustomerCommand;
import com.example.customer.api.dto.CustomerDTO;
import com.example.customer.app.assembler.CustomerAssembler;
import com.example.customer.domain.model.ContactInfo;
import com.example.customer.domain.model.Customer;
import com.example.customer.domain.model.CustomerLevel;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.domain.service.CustomerDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户应用服务
 */
@Slf4j
@Service
public class CustomerApplicationService {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;
    private final CustomerAssembler customerAssembler;

    public CustomerApplicationService(CustomerRepository customerRepository,
                                    CustomerDomainService customerDomainService,
                                    CustomerAssembler customerAssembler) {
        this.customerRepository = customerRepository;
        this.customerDomainService = customerDomainService;
        this.customerAssembler = customerAssembler;
    }

    /**
     * 创建客户
     *
     * @param command 创建客户命令
     * @return 客户DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO createCustomer(CreateCustomerCommand command) {
        // 检查客户名称是否已存在
        if (customerDomainService.isCustomerNameExists(command.getCustomerName())) {
            throw new BusinessException("客户名称已存在: " + command.getCustomerName());
        }
        
        // 检查手机号是否已存在
        if (command.getPhone() != null && !command.getPhone().isEmpty() && 
                customerDomainService.isPhoneExists(command.getPhone())) {
            throw new BusinessException("手机号已存在: " + command.getPhone());
        }
        
        // 检查邮箱是否已存在
        if (command.getEmail() != null && !command.getEmail().isEmpty() && 
                customerDomainService.isEmailExists(command.getEmail())) {
            throw new BusinessException("邮箱已存在: " + command.getEmail());
        }
        
        // 创建客户级别
        CustomerLevel customerLevel;
        if (command.getLevelCode() != null && !command.getLevelCode().isEmpty()) {
            switch (command.getLevelCode()) {
                case "VIP":
                    customerLevel = CustomerLevel.vipLevel();
                    break;
                case "VIP_PLUS":
                    customerLevel = CustomerLevel.vipPlusLevel();
                    break;
                default:
                    customerLevel = CustomerLevel.regularLevel();
            }
        } else {
            customerLevel = CustomerLevel.regularLevel();
        }
        
        // 创建联系方式
        ContactInfo contactInfo = command.getContactInfo();
        
        // 创建客户聚合根
        Customer customer = new Customer(command.getCustomerName(), customerLevel, contactInfo);
        
        // 保存客户
        customer = customerRepository.save(customer);
        
        // 转换为DTO
        return customerAssembler.toDTO(customer);
    }

    /**
     * 更新客户信息
     *
     * @param command 更新客户命令
     * @return 客户DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO updateCustomer(UpdateCustomerCommand command) {
        // 查询客户
        Customer customer = customerRepository.findById(command.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("客户不存在: " + command.getCustomerId()));
        
        // 检查客户名称是否已存在(排除自身)
        if (command.getCustomerName() != null && !command.getCustomerName().equals(customer.getCustomerName()) &&
                customerDomainService.isCustomerNameExists(command.getCustomerName())) {
            throw new BusinessException("客户名称已存在: " + command.getCustomerName());
        }
        
        // 检查手机号是否已存在(排除自身)
        if (command.getPhone() != null && !command.getPhone().isEmpty() && 
                !command.getPhone().equals(customer.getContactInfo().getPhone()) && 
                customerDomainService.isPhoneExists(command.getPhone())) {
            throw new BusinessException("手机号已存在: " + command.getPhone());
        }
        
        // 检查邮箱是否已存在(排除自身)
        if (command.getEmail() != null && !command.getEmail().isEmpty() && 
                !command.getEmail().equals(customer.getContactInfo().getEmail()) && 
                customerDomainService.isEmailExists(command.getEmail())) {
            throw new BusinessException("邮箱已存在: " + command.getEmail());
        }
        
        // 更新客户基本信息
        if (command.getCustomerName() != null && !command.getCustomerName().isEmpty()) {
            customer.updateBasicInfo(command.getCustomerName());
        }
        
        // 更新联系方式
        if ((command.getPhone() != null && !command.getPhone().isEmpty()) || 
                (command.getEmail() != null && !command.getEmail().isEmpty())) {
            ContactInfo contactInfo = new ContactInfo(
                    command.getPhone() != null ? command.getPhone() : customer.getContactInfo().getPhone(),
                    command.getEmail() != null ? command.getEmail() : customer.getContactInfo().getEmail()
            );
            customer.updateContactInfo(contactInfo);
        }
        
        // 更新客户级别
        if (command.getLevelCode() != null && !command.getLevelCode().isEmpty() && 
                !command.getLevelCode().equals(customer.getCustomerLevel().getLevelCode())) {
            // 调用领域服务升级客户级别
            customerDomainService.upgradeCustomerLevel(customer.getId(), command.getLevelCode());
        }
        
        // 保存客户
        customer = customerRepository.save(customer);
        
        // 转换为DTO
        return customerAssembler.toDTO(customer);
    }

    /**
     * 获取客户详情
     *
     * @param customerId 客户ID
     * @return 客户DTO
     */
    public CustomerDTO getCustomerById(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("客户不存在: " + customerId));
        return customerAssembler.toDTO(customer);
    }

    /**
     * 删除客户
     *
     * @param customerId 客户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("客户不存在: " + customerId);
        }
        customerRepository.deleteById(customerId);
        return true;
    }

    /**
     * 分页查询客户列表
     *
     * @param pageQuery 分页查询参数
     * @return 客户DTO列表
     */
    public List<CustomerDTO> listCustomers(PageQuery pageQuery) {
        List<Customer> customers = customerRepository.findByPage(pageQuery);
        return customers.stream()
                .map(customerAssembler::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 升级客户级别
     *
     * @param customerId 客户ID
     * @param levelCode  级别代码
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean upgradeCustomerLevel(String customerId, String levelCode) {
        customerDomainService.upgradeCustomerLevel(customerId, levelCode);
        return true;
    }
}
