package com.example.customer.api.feign;

import com.example.common.core.domain.PageQuery;
import com.example.common.core.domain.Result;
import com.example.customer.api.command.CreateCustomerCommand;
import com.example.customer.api.command.UpdateCustomerCommand;
import com.example.customer.api.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客户服务降级工厂
 */
@Slf4j
@Component
public class CustomerFeignFallbackFactory implements FallbackFactory<CustomerFeignClient> {

    @Override
    public CustomerFeignClient create(Throwable cause) {
        log.error("客户服务调用失败", cause);
        
        return new CustomerFeignClient() {
            @Override
            public Result<CustomerDTO> createCustomer(CreateCustomerCommand command) {
                return Result.failed("创建客户失败：" + cause.getMessage());
            }

            @Override
            public Result<CustomerDTO> updateCustomer(String customerId, UpdateCustomerCommand command) {
                return Result.failed("更新客户失败：" + cause.getMessage());
            }

            @Override
            public Result<CustomerDTO> getCustomerById(String customerId) {
                return Result.failed("获取客户信息失败：" + cause.getMessage());
            }

            @Override
            public Result<Boolean> deleteCustomer(String customerId) {
                return Result.failed("删除客户失败：" + cause.getMessage());
            }

            @Override
            public Result<List<CustomerDTO>> listCustomers(PageQuery pageQuery) {
                return Result.failed("查询客户列表失败：" + cause.getMessage());
            }

            @Override
            public Result<Boolean> upgradeCustomerLevel(String customerId, String levelCode) {
                return Result.failed("客户级别升级失败：" + cause.getMessage());
            }
        };
    }
}
