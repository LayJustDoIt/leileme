package com.leileme.common.vo;

import java.util.List;

/**
 * 通用分页结果。
 */
public record PageResult<T>(
        long page,
        long size,
        long total,
        boolean hasNext,
        List<T> list
) {}
