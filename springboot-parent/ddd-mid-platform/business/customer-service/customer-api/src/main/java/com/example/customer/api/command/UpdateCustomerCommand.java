package com.example.customer.api.command;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新客户命令
 */
@Data
public class UpdateCustomerCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 客户ID
     */
    @NotBlank(message = "客户ID不能为空")
    private String customerId;
    
    /**
     * 客户名称
     */
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
}
