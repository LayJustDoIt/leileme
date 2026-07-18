package com.leileme.search.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.service.ContentAssembler;
import com.leileme.search.entity.SearchRecord;
import com.leileme.search.mapper.SearchClickRecordMapper;
import com.leileme.search.mapper.SearchRecordMapper;
import com.leileme.search.mapper.model.SearchContentRow;
import com.leileme.search.vo.SearchResultVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {
    @Test
    void shouldReturnMatchedContentAndPersistSearchRecord() {
        ContentMapper contentMapper = mock(ContentMapper.class);
        SearchRecordMapper recordMapper = mock(SearchRecordMapper.class);
        SearchClickRecordMapper clickMapper = mock(SearchClickRecordMapper.class);
        ContentAssembler assembler = new ContentAssembler(contentMapper);
        SearchService service = new SearchService(contentMapper, recordMapper, clickMapper, assembler);

        SearchContentRow row = new SearchContentRow();
        row.setId(1004L);
        row.setContentType("COPYWRITING");
        row.setTitle("短视频爆款开头的 10 种写法");
        row.setSummary("适合创作者");
        row.setViewCount(10L);
        row.setFavoriteCount(1L);
        row.setPublishedAt(LocalDateTime.now());

        Page<SearchContentRow> result = new Page<>(1, 20);
        result.setRecords(List.of(row));
        result.setTotal(1);
        when(contentMapper.selectSearchPage(any(), eq("爆款文案"), isNull(), isNull())).thenReturn(result);
        when(contentMapper.selectTagNames(1004L)).thenReturn(List.of("爆款文案"));
        when(recordMapper.insert(any(SearchRecord.class))).thenReturn(1);

        SearchResultVO response = service.search(" 爆款文案 ", null, null, 1, 20, null, "s1");

        assertEquals(1, response.total());
        assertEquals("爆款文案", response.keyword());
        assertFalse(response.fallback());
        assertEquals(1004L, response.list().getFirst().id());
        verify(recordMapper).insert(any(SearchRecord.class));
    }
}
