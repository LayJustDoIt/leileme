package com.leileme.common.response;

import java.util.List;

public record PageResponse<T>(long page, long size, long total, boolean hasNext, List<T> list) {
    public static <T> PageResponse<T> of(long page, long size, long total, List<T> list) {
        return new PageResponse<>(page, size, total, page * size < total, list);
    }
}
