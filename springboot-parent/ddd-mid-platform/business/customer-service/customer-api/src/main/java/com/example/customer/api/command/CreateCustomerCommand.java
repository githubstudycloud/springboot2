package com.example.customer.api.command;

import com.example.customer.api.dto.AddressDTO;
import com.example.customer.api.dto.ContactDTO;
import com.example.customer.api.dto.ContactInfoDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 创建客户命令
 */
@Data
public class CreateCustomerCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 100, message = "客户名称长度不能超过100个字符")
    private String customerName;
    
    /**
     * 电话号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 客户级别代码
     */
    private String levelCode;
    
    /**
     * 地址列表
     */
    @Valid
    private List<AddressDTO> addresses;
    
    /**
     * 联系人列表
     */
    @Valid
    private List<ContactDTO> contacts;
    
    /**
     * 获取联系方式
     */
    public ContactInfoDTO getContactInfo() {
        return new ContactInfoDTO(phone, email);
    }
}
