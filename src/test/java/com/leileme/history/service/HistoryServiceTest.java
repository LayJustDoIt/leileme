package com.leileme.history.service;

import com.leileme.content.mapper.ContentMapper;
import com.leileme.history.entity.UserBrowseRecord;
import com.leileme.history.mapper.UserBrowseRecordMapper;
import com.leileme.user.mapper.UserFavoriteMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 浏览历史服务测试：
 * - 浏览记录重复访问正确累加 browse_count
 * - 匿名浏览历史登录后正确合并到 userId
 */
class HistoryServiceTest {

    @Test
    void repeatBrowseAccumulatesCount() {
        UserBrowseRecordMapper recordMapper = mock(UserBrowseRecordMapper.class);
        ContentMapper contentMapper = mock(ContentMapper.class);
        UserFavoriteMapper favMapper = mock(UserFavoriteMapper.class);
        HistoryService service = new HistoryService(recordMapper, contentMapper, favMapper);

        // 已存在记录：browseCount=1
        UserBrowseRecord existing = new UserBrowseRecord();
        existing.setId(10L);
        existing.setUserId(1L);
        existing.setContentId(1001L);
        existing.setBrowseCount(1);
        existing.setFirstBrowsedAt(LocalDateTime.now().minusDays(1));
        existing.setLastBrowsedAt(LocalDateTime.now().minusHours(2));
        when(recordMapper.selectOne(any())).thenReturn(existing);

        service.recordBrowse(1001L, 1L, null);

        // browseCount 应累加为 2
        assertEquals(2, existing.getBrowseCount());
        // lastBrowsedAt 应更新为当前附近时间
        assertNotNull(existing.getLastBrowsedAt());
        // 应更新而非新增
        verify(recordMapper).updateById(existing);
        verify(recordMapper, never()).insert(any(UserBrowseRecord.class));
    }

    @Test
    void anonymousHistoryMergedToUserOnLogin() {
        UserBrowseRecordMapper recordMapper = mock(UserBrowseRecordMapper.class);
        ContentMapper contentMapper = mock(ContentMapper.class);
        UserFavoriteMapper favMapper = mock(UserFavoriteMapper.class);
        HistoryService service = new HistoryService(recordMapper, contentMapper, favMapper);

        // 匿名会话有一条浏览记录（contentId=1001, browseCount=2，最近 1 小时浏览过）
        LocalDateTime anonFirst = LocalDateTime.now().minusDays(2);
        LocalDateTime anonLast = LocalDateTime.now().minusHours(1);
        UserBrowseRecord sessionRec = new UserBrowseRecord();
        sessionRec.setId(20L);
        sessionRec.setSessionId("sess-anon");
        sessionRec.setContentId(1001L);
        sessionRec.setBrowseCount(2);
        sessionRec.setFirstBrowsedAt(anonFirst);
        sessionRec.setLastBrowsedAt(anonLast);
        when(recordMapper.selectList(any())).thenReturn(List.of(sessionRec));

        // 用户已有同一内容的记录（browseCount=1，浏览时间更早）
        LocalDateTime userFirst = LocalDateTime.now().minusDays(3);
        LocalDateTime userLast = LocalDateTime.now().minusHours(5); // 早于 anonLast
        UserBrowseRecord userExisting = new UserBrowseRecord();
        userExisting.setId(30L);
        userExisting.setUserId(1L);
        userExisting.setContentId(1001L);
        userExisting.setBrowseCount(1);
        userExisting.setFirstBrowsedAt(userFirst);
        userExisting.setLastBrowsedAt(userLast);
        when(recordMapper.selectOne(any())).thenReturn(userExisting);

        service.mergeAnonymousData(1L, "sess-anon");

        // 合并后 browseCount = 1 + 2 = 3
        assertEquals(3, userExisting.getBrowseCount());
        // lastBrowsedAt 取较新值（anonLast 更近）
        assertEquals(anonLast, userExisting.getLastBrowsedAt());
        // firstBrowsedAt 取较早值（userFirst 更早）
        assertEquals(userFirst, userExisting.getFirstBrowsedAt());
        // 合并后删除匿名记录
        verify(recordMapper).deleteById(20L);
        verify(recordMapper).updateById(userExisting);
    }
}
