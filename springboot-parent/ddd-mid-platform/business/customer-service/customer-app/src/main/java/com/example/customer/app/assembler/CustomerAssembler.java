package com.example.customer.app.assembler;

import com.example.customer.api.dto.*;
import com.example.customer.app.repository.entity.AddressDO;
import com.example.customer.app.repository.entity.ContactDO;
import com.example.customer.app.repository.entity.CustomerDO;
import com.example.customer.domain.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 客户数据转换器
 */
@Component
public class CustomerAssembler {

    /**
     * 将领域模型转换为DTO
     */
    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setCustomerLevel(toCustomerLevelDTO(customer.getCustomerLevel()));
        dto.setContactInfo(toContactInfoDTO(customer.getContactInfo()));
        dto.setAddresses(toAddressDTOs(customer.getAddresses()));
        dto.setContacts(toContactDTOs(customer.getContacts()));
        dto.setCreatedTime(customer.getCreatedTime());
        dto.setLastModifiedTime(customer.getLastModifiedTime());
        return dto;
    }

    /**
     * 将客户级别领域模型转换为DTO
     */
    public CustomerLevelDTO toCustomerLevelDTO(CustomerLevel customerLevel) {
        if (customerLevel == null) {
            return null;
        }
        return new CustomerLevelDTO(
                customerLevel.getLevelCode(),
                customerLevel.getLevelName(),
                customerLevel.getDiscountRate()
        );
    }

    /**
     * 将联系方式领域模型转换为DTO
     */
    public ContactInfoDTO toContactInfoDTO(ContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return new ContactInfoDTO(
                contactInfo.getPhone(),
                contactInfo.getEmail()
        );
    }

    /**
     * 将地址领域模型列表转换为DTO列表
     */
    public List<AddressDTO> toAddressDTOs(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return Collections.emptyList();
        }
        return addresses.stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将地址领域模型转换为DTO
     */
    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getId());
        dto.setAddressType(address.getAddressType());
        dto.setCountry(address.getCountry());
        dto.setProvince(address.getProvince());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setDetailAddress(address.getDetailAddress());
        dto.setZipCode(address.getZipCode());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }

    /**
     * 将联系人领域模型列表转换为DTO列表
     */
    public List<ContactDTO> toContactDTOs(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return Collections.emptyList();
        }
        return contacts.stream()
                .map(this::toContactDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将联系人领域模型转换为DTO
     */
    public ContactDTO toContactDTO(Contact contact) {
        if (contact == null) {
            return null;
        }
        ContactDTO dto = new ContactDTO();
        dto.setContactId(contact.getId());
        dto.setName(contact.getName());
        dto.setPhone(contact.getPhone());
        dto.setEmail(contact.getEmail());
        dto.setPosition(contact.getPosition());
        dto.setDepartment(contact.getDepartment());
        dto.setIsPrimary(contact.getIsPrimary());
        dto.setRemark(contact.getRemark());
        return dto;
    }

    /**
     * 将DO对象转换为领域模型
     */
    public Customer toCustomer(CustomerDO customerDO, List<AddressDO> addressDOs, List<ContactDO> contactDOs) {
        if (customerDO == null) {
            return null;
        }

        // 创建客户级别
        CustomerLevel customerLevel = new CustomerLevel(
                customerDO.getLevelCode(),
                customerDO.getLevelName(),
                new BigDecimal(customerDO.getDiscountRate())
        );

        // 创建联系方式
        ContactInfo contactInfo = new ContactInfo(
                customerDO.getPhone(),
                customerDO.getEmail()
        );

        // 反射创建客户对象
        Customer customer;
        try {
            // 使用反射方式创建聚合根，保持其完整性
            customer = Customer.class.getDeclaredConstructor().newInstance();
            
            // 通过反射设置私有字段
            java.lang.reflect.Field customerIdField = Customer.class.getDeclaredField("customerId");
            customerIdField.setAccessible(true);
            customerIdField.set(customer, customerDO.getCustomerId());
            
            java.lang.reflect.Field customerNameField = Customer.class.getDeclaredField("customerName");
            customerNameField.setAccessible(true);
            customerNameField.set(customer, customerDO.getCustomerName());
            
            java.lang.reflect.Field customerLevelField = Customer.class.getDeclaredField("customerLevel");
            customerLevelField.setAccessible(true);
            customerLevelField.set(customer, customerLevel);
            
            java.lang.reflect.Field contactInfoField = Customer.class.getDeclaredField("contactInfo");
            contactInfoField.setAccessible(true);
            contactInfoField.set(customer, contactInfo);
            
            // 设置版本号和审计字段
            customer.setVersion(customerDO.getVersion());
            customer.setCreatedTime(customerDO.getCreatedTime());
            customer.setLastModifiedTime(customerDO.getLastModifiedTime());
            
            // 添加地址列表
            if (addressDOs != null && !addressDOs.isEmpty()) {
                for (AddressDO addressDO : addressDOs) {
                    Address address = toAddress(addressDO);
                    if (address != null) {
                        java.lang.reflect.Method addAddressMethod = Customer.class.getDeclaredMethod("addAddress", Address.class);
                        addAddressMethod.invoke(customer, address);
                    }
                }
            }
            
            // 添加联系人列表
            if (contactDOs != null && !contactDOs.isEmpty()) {
                for (ContactDO contactDO : contactDOs) {
                    Contact contact = toContact(contactDO);
                    if (contact != null) {
                        java.lang.reflect.Method addContactMethod = Customer.class.getDeclaredMethod("addContact", Contact.class);
                        addContactMethod.invoke(customer, contact);
                    }
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Customer from DO", e);
        }
        
        return customer;
    }

    /**
     * 将地址DO转换为领域模型
     */
    public Address toAddress(AddressDO addressDO) {
        if (addressDO == null) {
            return null;
        }
        return new Address(
                addressDO.getAddressId(),
                addressDO.getAddressType(),
                addressDO.getCountry(),
                addressDO.getProvince(),
                addressDO.getCity(),
                addressDO.getDistrict(),
                addressDO.getDetailAddress(),
                addressDO.getZipCode(),
                addressDO.getIsDefault(),
                addressDO.getCustomerId()
        );
    }

    /**
     * 将联系人DO转换为领域模型
     */
    public Contact toContact(ContactDO contactDO) {
        if (contactDO == null) {
            return null;
        }
        return new Contact(
                contactDO.getContactId(),
                contactDO.getName(),
                contactDO.getPhone(),
                contactDO.getEmail(),
                contactDO.getPosition(),
                contactDO.getDepartment(),
                contactDO.getIsPrimary(),
                contactDO.getRemark(),
                contactDO.getCustomerId()
        );
    }

    /**
     * 将领域模型转换为CustomerDO
     */
    public CustomerDO toCustomerDO(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerDO customerDO = new CustomerDO();
        customerDO.setCustomerId(customer.getId());
        customerDO.setCustomerName(customer.getCustomerName());
        customerDO.setPhone(customer.getContactInfo().getPhone());
        customerDO.setEmail(customer.getContactInfo().getEmail());
        customerDO.setLevelCode(customer.getCustomerLevel().getLevelCode());
        customerDO.setLevelName(customer.getCustomerLevel().getLevelName());
        customerDO.setDiscountRate(customer.getCustomerLevel().getDiscountRate().toString());
        customerDO.setVersion(customer.getVersion());
        customerDO.setCreatedTime(customer.getCreatedTime());
        customerDO.setLastModifiedTime(customer.getLastModifiedTime());
        customerDO.setDeleted(false);
        return customerDO;
    }

    /**
     * 将领域模型转换为地址DO列表
     */
    public List<AddressDO> toAddressDOs(Customer customer) {
        if (customer == null || customer.getAddresses().isEmpty()) {
            return Collections.emptyList();
        }
        List<AddressDO> addressDOs = new ArrayList<>(customer.getAddresses().size());
        for (Address address : customer.getAddresses()) {
            AddressDO addressDO = new AddressDO();
            addressDO.setAddressId(address.getId());
            addressDO.setCustomerId(customer.getId());
            addressDO.setAddressType(address.getAddressType());
            addressDO.setCountry(address.getCountry());
            addressDO.setProvince(address.getProvince());
            addressDO.setCity(address.getCity());
            addressDO.setDistrict(address.getDistrict());
            addressDO.setDetailAddress(address.getDetailAddress());
            addressDO.setZipCode(address.getZipCode());
            addressDO.setIsDefault(address.getIsDefault());
            addressDO.setDeleted(false);
            addressDOs.add(addressDO);
        }
        return addressDOs;
    }

    /**
     * 将领域模型转换为联系人DO列表
     */
    public List<ContactDO> toContactDOs(Customer customer) {
        if (customer == null || customer.getContacts().isEmpty()) {
            return Collections.emptyList();
        }
        List<ContactDO> contactDOs = new ArrayList<>(customer.getContacts().size());
        for (Contact contact : customer.getContacts()) {
            ContactDO contactDO = new ContactDO();
            contactDO.setContactId(contact.getId());
            contactDO.setCustomerId(customer.getId());
            contactDO.setName(contact.getName());
            contactDO.setPhone(contact.getPhone());
            contactDO.setEmail(contact.getEmail());
            contactDO.setPosition(contact.getPosition());
            contactDO.setDepartment(contact.getDepartment());
            contactDO.setIsPrimary(contact.getIsPrimary());
            contactDO.setRemark(contact.getRemark());
            contactDO.setDeleted(false);
            contactDOs.add(contactDO);
        }
        return contactDOs;
    }

    /**
     * DTO转换为领域模型(创建新客户)
     */
    public Customer toCustomer(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        // 创建客户级别
        CustomerLevel customerLevel = new CustomerLevel(
                dto.getCustomerLevel().getLevelCode(),
                dto.getCustomerLevel().getLevelName(),
                dto.getCustomerLevel().getDiscountRate()
        );

        // 创建联系方式
        ContactInfo contactInfo = new ContactInfo(
                dto.getContactInfo().getPhone(),
                dto.getContactInfo().getEmail()
        );

        // 创建新客户
        Customer customer = new Customer(dto.getCustomerName(), customerLevel, contactInfo);
        
        // 添加地址
        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            for (AddressDTO addressDTO : dto.getAddresses()) {
                Address address = new Address(
                        generateId(),
                        addressDTO.getAddressType(),
                        addressDTO.getCountry(),
                        addressDTO.getProvince(),
                        addressDTO.getCity(),
                        addressDTO.getDistrict(),
                        addressDTO.getDetailAddress(),
                        addressDTO.getZipCode(),
                        addressDTO.getIsDefault(),
                        customer.getId()
                );
                customer.addAddress(address);
            }
        }
        
        // 添加联系人
        if (dto.getContacts() != null && !dto.getContacts().isEmpty()) {
            for (ContactDTO contactDTO : dto.getContacts()) {
                Contact contact = new Contact(
                        generateId(),
                        contactDTO.getName(),
                        contactDTO.getPhone(),
                        contactDTO.getEmail(),
                        contactDTO.getPosition(),
                        contactDTO.getDepartment(),
                        contactDTO.getIsPrimary(),
                        contactDTO.getRemark(),
                        customer.getId()
                );
                customer.addContact(contact);
            }
        }
        
        return customer;
    }
    
    /**
     * 生成唯一ID
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
