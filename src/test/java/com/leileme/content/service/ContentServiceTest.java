package com.leileme.content.service;

import com.leileme.content.entity.Content;
import com.leileme.history.entity.UserBrowseRecord;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.vo.ContentDetailVO;
import com.leileme.history.mapper.UserBrowseRecordMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContentServiceTest {
    @Test
    void shouldReturnPublishedContentDetail() {
        ContentMapper contentMapper = mock(ContentMapper.class);
        UserBrowseRecordMapper browseMapper = mock(UserBrowseRecordMapper.class);
        ContentAssembler assembler = new ContentAssembler(contentMapper);
        ContentService service = new ContentService(contentMapper, browseMapper, assembler);

        Content content = new Content();
        content.setId(1001L);
        content.setCategoryId(1L);
        content.setContentType("HOT_NEWS");
        content.setTitle("今日热搜");
        content.setStatus(1);
        content.setViewCount(10L);
        content.setFavoriteCount(2L);
        content.setShareCount(1L);
        content.setPublishedAt(LocalDateTime.now());

        when(contentMapper.selectById(1001L)).thenReturn(content);
        when(contentMapper.incrementViewCount(1001L)).thenReturn(1);
        when(contentMapper.selectTagNames(1001L)).thenReturn(List.of("今日热搜"));
        when(contentMapper.selectRelated(1L, 1001L, 4)).thenReturn(List.of());
        when(browseMapper.insert(any(UserBrowseRecord.class))).thenReturn(1);

        ContentDetailVO detail = service.getDetail(1001L, null, "session");

        assertEquals("今日热搜", detail.title());
        assertEquals(11L, detail.viewCount());
        verify(browseMapper).insert(any(UserBrowseRecord.class));
    }
}
