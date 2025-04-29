package com.example.customer.app.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.domain.PageQuery;
import com.example.customer.app.assembler.CustomerAssembler;
import com.example.customer.app.repository.entity.AddressDO;
import com.example.customer.app.repository.entity.ContactDO;
import com.example.customer.app.repository.entity.CustomerDO;
import com.example.customer.app.repository.mapper.AddressMapper;
import com.example.customer.app.repository.mapper.ContactMapper;
import com.example.customer.app.repository.mapper.CustomerMapper;
import com.example.customer.domain.model.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客户仓储实现类
 */
@Slf4j
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final ContactMapper contactMapper;
    private final CustomerAssembler customerAssembler;

    public CustomerRepositoryImpl(CustomerMapper customerMapper, AddressMapper addressMapper,
                                 ContactMapper contactMapper, CustomerAssembler customerAssembler) {
        this.customerMapper = customerMapper;
        this.addressMapper = addressMapper;
        this.contactMapper = contactMapper;
        this.customerAssembler = customerAssembler;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer save(Customer customer) {
        // 转换为DO对象
        CustomerDO customerDO = customerAssembler.toCustomerDO(customer);
        List<AddressDO> addressDOs = customerAssembler.toAddressDOs(customer);
        List<ContactDO> contactDOs = customerAssembler.toContactDOs(customer);

        // 处理版本号，用于乐观锁
        if (customer.getVersion() == null) {
            customerDO.setVersion(1L);
        } else {
            customerDO.setVersion(customer.getVersion() + 1);
        }

        // 保存或更新客户信息
        if (customerMapper.selectById(customerDO.getCustomerId()) == null) {
            // 新增
            customerMapper.insert(customerDO);
        } else {
            // 更新
            customerMapper.updateById(customerDO);
        }

        // 处理地址列表
        if (addressDOs != null && !addressDOs.isEmpty()) {
            // 先删除旧地址
            addressMapper.deleteByCustomerId(customer.getId());
            // 再插入新地址
            for (AddressDO addressDO : addressDOs) {
                addressMapper.insert(addressDO);
            }
        }

        // 处理联系人列表
        if (contactDOs != null && !contactDOs.isEmpty()) {
            // 先删除旧联系人
            contactMapper.deleteByCustomerId(customer.getId());
            // 再插入新联系人
            for (ContactDO contactDO : contactDOs) {
                contactMapper.insert(contactDO);
            }
        }

        // 重新查询完整客户信息
        return findById(customer.getId()).orElse(null);
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        // 查询客户基本信息
        CustomerDO customerDO = customerMapper.selectById(customerId);
        if (customerDO == null) {
            return Optional.empty();
        }

        // 查询客户地址列表
        List<AddressDO> addressDOs = addressMapper.selectByCustomerId(customerId);

        // 查询客户联系人列表
        List<ContactDO> contactDOs = contactMapper.selectByCustomerId(customerId);

        // 转换为聚合根
        Customer customer = customerAssembler.toCustomer(customerDO, addressDOs, contactDOs);
        return Optional.of(customer);
    }

    @Override
    public boolean existsById(String customerId) {
        return customerMapper.selectById(customerId) != null;
    }

    @Override
    public List<Customer> findAll() {
        // 查询所有客户
        List<CustomerDO> customerDOs = customerMapper.selectList(null);
        return customerDOs.stream()
                .map(customerDO -> findById(customerDO.getCustomerId()).orElse(null))
                .filter(customer -> customer != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String customerId) {
        // 删除客户关联的地址和联系人
        addressMapper.deleteByCustomerId(customerId);
        contactMapper.deleteByCustomerId(customerId);
        // 删除客户
        customerMapper.deleteById(customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Customer customer) {
        deleteById(customer.getId());
    }

    @Override
    public long count() {
        return customerMapper.selectCount(null);
    }

    @Override
    public List<Customer> findByCustomerName(String customerName) {
        LambdaQueryWrapper<CustomerDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CustomerDO::getCustomerName, customerName);
        List<CustomerDO> customerDOs = customerMapper.selectList(wrapper);
        return customerDOs.stream()
                .map(customerDO -> findById(customerDO.getCustomerId()).orElse(null))
                .filter(customer -> customer != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByLevelCode(String levelCode) {
        List<CustomerDO> customerDOs = customerMapper.selectByLevelCode(levelCode);
        return customerDOs.stream()
                .map(customerDO -> findById(customerDO.getCustomerId()).orElse(null))
                .filter(customer -> customer != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByPhone(String phone) {
        List<CustomerDO> customerDOs = customerMapper.selectByPhone(phone);
        return customerDOs.stream()
                .map(customerDO -> findById(customerDO.getCustomerId()).orElse(null))
                .filter(customer -> customer != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByEmail(String email) {
        List<CustomerDO> customerDOs = customerMapper.selectByEmail(email);
        return customerDOs.stream()
                .map(customerDO -> findById(customerDO.getCustomerId()).orElse(null))
                .filter(customer -> customer != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByPage(PageQuery pageQuery) {
        // 构建分页对象
        Page<CustomerDO> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        
        // 执行分页查询
        Page<CustomerDO> resultPage = customerMapper.selectPage(page, null);
        
        // 转换结果
        return resultPage.getRecords().stream()
                .map(customerDO -> findById(customerDO.getCustomerId()).orElse(null))
                .filter(customer -> customer != null)
                .collect(Collectors.toList());
    }

    @Override
    public long countAll() {
        return customerMapper.selectCount(null);
    }
}
