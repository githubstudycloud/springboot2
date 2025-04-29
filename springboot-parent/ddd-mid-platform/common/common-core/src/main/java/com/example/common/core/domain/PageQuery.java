package com.example.common.core.domain;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询参数
 */
@Data
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页记录数
     */
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否降序
     */
    private Boolean isDesc = false;
}
