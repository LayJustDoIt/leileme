package com.leileme.search.vo;

import com.leileme.content.vo.ContentCardVO;

import java.util.List;

public record SearchResultVO(
        String searchRequestId,
        String keyword,
        long page,
        long size,
        long total,
        boolean hasNext,
        List<SearchFilterVO> contentTypes,
        List<ContentCardVO> list,
        boolean fallback
) {}
