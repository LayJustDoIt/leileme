package com.leileme.search.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.common.exception.BusinessException;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.service.ContentAssembler;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.search.entity.SearchClickRecord;
import com.leileme.search.entity.SearchRecord;
import com.leileme.search.mapper.SearchClickRecordMapper;
import com.leileme.search.mapper.SearchRecordMapper;
import com.leileme.search.mapper.model.SearchContentRow;
import com.leileme.search.vo.SearchFilterVO;
import com.leileme.search.vo.SearchResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private static final Map<String, String> TYPE_NAMES = Map.of(
            "HOT_NEWS", "热点资讯",
            "COPYWRITING", "文案脚本",
            "TOOL", "工具资源",
            "GUIDE", "方法教程",
            "RANDOM_TIP", "随机建议"
    );

    private final ContentMapper contentMapper;
    private final SearchRecordMapper searchRecordMapper;
    private final SearchClickRecordMapper searchClickRecordMapper;
    private final ContentAssembler assembler;

    public SearchService(ContentMapper contentMapper, SearchRecordMapper searchRecordMapper,
                         SearchClickRecordMapper searchClickRecordMapper, ContentAssembler assembler) {
        this.contentMapper = contentMapper;
        this.searchRecordMapper = searchRecordMapper;
        this.searchClickRecordMapper = searchClickRecordMapper;
        this.assembler = assembler;
    }

    public SearchResultVO search(String rawKeyword, String contentType, Long categoryId,
                                 long page, long size, Long userId, String sessionId) {
        long started = System.currentTimeMillis();
        String keyword = normalize(rawKeyword);
        page = Math.max(1, page);
        size = Math.min(50, Math.max(1, size));
        String searchRequestId = "SR" + UUID.randomUUID().toString().replace("-", "");

        Page<SearchContentRow> pageRequest = new Page<>(page, size);
        IPage<SearchContentRow> result = contentMapper.selectSearchPage(pageRequest, keyword, blankToNull(contentType), categoryId);
        boolean fallback = false;
        List<ContentCardVO> cards = result.getRecords().stream().map(assembler::fromSearchRow).toList();

        if (cards.isEmpty()) {
            fallback = true;
            List<Content> fallbackContents = contentMapper.selectList(Wrappers.lambdaQuery(Content.class)
                    .eq(Content::getStatus, 1)
                    .ne(Content::getContentType, "RANDOM_TIP")
                    .orderByDesc(Content::getHotScore)
                    .last("LIMIT 10"));
            cards = fallbackContents.stream().map(assembler::fromEntity).toList();
        }

        Map<String, Long> counts = cards.stream().collect(Collectors.groupingBy(
                ContentCardVO::contentType, LinkedHashMap::new, Collectors.counting()));
        List<SearchFilterVO> filters = counts.entrySet().stream()
                .map(e -> new SearchFilterVO(e.getKey(), TYPE_NAMES.getOrDefault(e.getKey(), e.getKey()), e.getValue()))
                .toList();

        SearchRecord record = new SearchRecord();
        record.setRequestId(searchRequestId);
        record.setUserId(userId);
        record.setSessionId(sessionId);
        record.setOriginalKeyword(rawKeyword);
        record.setNormalizedKeyword(keyword);
        record.setContentType(blankToNull(contentType));
        record.setCategoryId(categoryId);
        record.setResultCount(Math.toIntExact(result.getTotal()));
        record.setResponseTimeMs((int) (System.currentTimeMillis() - started));
        record.setCreatedAt(LocalDateTime.now());
        searchRecordMapper.insert(record);

        return new SearchResultVO(searchRequestId, keyword, page, size, result.getTotal(),
                page * size < result.getTotal(), filters, cards, fallback);
    }

    @Transactional
    public void recordClick(String requestId, Long contentId, Integer position,
                            Long userId, String sessionId) {
        SearchClickRecord record = new SearchClickRecord();
        record.setRequestId(requestId);
        record.setContentId(contentId);
        record.setResultPosition(position);
        record.setUserId(userId);
        record.setSessionId(sessionId);
        record.setCreatedAt(LocalDateTime.now());
        searchClickRecordMapper.insert(record);
    }

    private String normalize(String rawKeyword) {
        if (rawKeyword == null || rawKeyword.isBlank()) {
            throw new BusinessException(40002, "搜索关键词不能为空");
        }
        String normalized = rawKeyword.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        if (normalized.length() > 100) {
            throw new BusinessException(40003, "搜索关键词过长");
        }
        return normalized;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
