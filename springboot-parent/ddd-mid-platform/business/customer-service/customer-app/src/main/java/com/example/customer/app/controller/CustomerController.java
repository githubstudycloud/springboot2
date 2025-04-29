package com.example.customer.app.controller;

import com.example.common.core.domain.PageQuery;
import com.example.common.core.domain.Result;
import com.example.customer.api.command.CreateCustomerCommand;
import com.example.customer.api.command.UpdateCustomerCommand;
import com.example.customer.api.dto.CustomerDTO;
import com.example.customer.app.service.CustomerApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 */
@Slf4j
@RestController
@RequestMapping("/customers")
@Tag(name = "客户管理", description = "客户管理相关接口")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    /**
     * 创建客户
     */
    @PostMapping
    @Operation(summary = "创建客户", description = "创建新客户")
    public Result<CustomerDTO> createCustomer(@Validated @RequestBody CreateCustomerCommand command) {
        log.info("创建客户: {}", command.getCustomerName());
        CustomerDTO customerDTO = customerApplicationService.createCustomer(command);
        return Result.success(customerDTO);
    }

    /**
     * 更新客户
     */
    @PutMapping("/{customerId}")
    @Operation(summary = "更新客户", description = "更新客户信息")
    public Result<CustomerDTO> updateCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable String customerId,
            @Validated @RequestBody UpdateCustomerCommand command) {
        log.info("更新客户: {}", customerId);
        command.setCustomerId(customerId);
        CustomerDTO customerDTO = customerApplicationService.updateCustomer(command);
        return Result.success(customerDTO);
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{customerId}")
    @Operation(summary = "获取客户详情", description = "根据ID获取客户详情")
    public Result<CustomerDTO> getCustomerById(
            @Parameter(description = "客户ID", required = true) @PathVariable String customerId) {
        log.info("获取客户详情: {}", customerId);
        CustomerDTO customerDTO = customerApplicationService.getCustomerById(customerId);
        return Result.success(customerDTO);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{customerId}")
    @Operation(summary = "删除客户", description = "根据ID删除客户")
    public Result<Boolean> deleteCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable String customerId) {
        log.info("删除客户: {}", customerId);
        boolean result = customerApplicationService.deleteCustomer(customerId);
        return Result.success(result);
    }

    /**
     * 分页查询客户列表
     */
    @GetMapping
    @Operation(summary = "查询客户列表", description = "分页查询客户列表")
    public Result<List<CustomerDTO>> listCustomers(PageQuery pageQuery) {
        log.info("查询客户列表: page={}, size={}", pageQuery.getPageNum(), pageQuery.getPageSize());
        List<CustomerDTO> customerDTOs = customerApplicationService.listCustomers(pageQuery);
        return Result.success(customerDTOs);
    }

    /**
     * 客户级别升级
     */
    @PostMapping("/{customerId}/level/{levelCode}")
    @Operation(summary = "客户级别升级", description = "将客户级别升级到指定级别")
    public Result<Boolean> upgradeCustomerLevel(
            @Parameter(description = "客户ID", required = true) @PathVariable String customerId,
            @Parameter(description = "级别代码", required = true) @PathVariable String levelCode) {
        log.info("客户级别升级: {} -> {}", customerId, levelCode);
        boolean result = customerApplicationService.upgradeCustomerLevel(customerId, levelCode);
        return Result.success(result);
    }
}
