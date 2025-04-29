package com.example.customer.api.feign;

import com.example.common.core.domain.PageQuery;
import com.example.common.core.domain.Result;
import com.example.customer.api.command.CreateCustomerCommand;
import com.example.customer.api.command.UpdateCustomerCommand;
import com.example.customer.api.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户服务Feign客户端
 */
@FeignClient(name = "customer-service", path = "/customer", fallbackFactory = CustomerFeignFallbackFactory.class)
public interface CustomerFeignClient {

    /**
     * 创建客户
     *
     * @param command 创建客户命令
     * @return 客户信息
     */
    @PostMapping("/customers")
    Result<CustomerDTO> createCustomer(@RequestBody CreateCustomerCommand command);

    /**
     * 更新客户
     *
     * @param command 更新客户命令
     * @return 客户信息
     */
    @PutMapping("/customers/{customerId}")
    Result<CustomerDTO> updateCustomer(@PathVariable("customerId") String customerId, @RequestBody UpdateCustomerCommand command);

    /**
     * 获取客户详情
     *
     * @param customerId 客户ID
     * @return 客户信息
     */
    @GetMapping("/customers/{customerId}")
    Result<CustomerDTO> getCustomerById(@PathVariable("customerId") String customerId);

    /**
     * 删除客户
     *
     * @param customerId 客户ID
     * @return 操作结果
     */
    @DeleteMapping("/customers/{customerId}")
    Result<Boolean> deleteCustomer(@PathVariable("customerId") String customerId);

    /**
     * 分页查询客户列表
     *
     * @param pageQuery 分页查询参数
     * @return 客户列表
     */
    @GetMapping("/customers")
    Result<List<CustomerDTO>> listCustomers(PageQuery pageQuery);

    /**
     * 客户级别升级
     *
     * @param customerId 客户ID
     * @param levelCode  级别代码
     * @return 操作结果
     */
    @PostMapping("/customers/{customerId}/level/{levelCode}")
    Result<Boolean> upgradeCustomerLevel(@PathVariable("customerId") String customerId, @PathVariable("levelCode") String levelCode);
}
