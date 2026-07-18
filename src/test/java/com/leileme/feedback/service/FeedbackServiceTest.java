package com.leileme.feedback.service;

import com.leileme.common.auth.UserContext;
import com.leileme.common.exception.BusinessException;
import com.leileme.feedback.config.FeedbackProperties;
import com.leileme.feedback.dto.FeedbackRequest;
import com.leileme.feedback.entity.FeedbackRecord;
import com.leileme.feedback.mapper.FeedbackRecordMapper;
import com.leileme.feedback.vo.FeedbackResultVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 反馈服务测试：
 * - 匿名用户提交成功（带 sessionId，user_id 为空）
 * - 登录用户提交成功（带 userId，不保存 sessionId）
 * - content 过短拒绝
 * - contact 过长拒绝
 * - 匿名用户缺少 sessionId 拒绝
 * - 同一身份 + 相同 content + 相同 contact + 60s 内重复提交拒绝（42901）
 * - 同一身份 + 不同 content 允许连续提交
 */
class FeedbackServiceTest {

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    private FeedbackProperties props() {
        FeedbackProperties p = new FeedbackProperties();
        p.setMinIntervalSeconds(60L);
        p.setDailyLimit(10);
        return p;
    }

    @Test
    void anonymousSubmitSuccess() {
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        when(mapper.selectCount(any())).thenReturn(0L);
        ArgumentCaptor<FeedbackRecord> captor = ArgumentCaptor.forClass(FeedbackRecord.class);
        doAnswer(invocation -> {
            FeedbackRecord r = invocation.getArgument(0);
            r.setId(101L);
            return 1;
        }).when(mapper).insert(captor.capture());

        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("这是一条匿名反馈内容。", null, "pages/mine/mine", "sess-anon-001");

        FeedbackResultVO result = service.submit(req);

        assertEquals(101L, result.id());
        FeedbackRecord saved = captor.getValue();
        assertNull(saved.getUserId());
        assertEquals("sess-anon-001", saved.getSessionId());
        assertEquals("这是一条匿名反馈内容。", saved.getContent());
        assertEquals("pages/mine/mine", saved.getPagePath());
    }

    @Test
    void loggedInUserSubmitSuccessWithUserId() {
        UserContext.set(99L);
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        when(mapper.selectCount(any())).thenReturn(0L);
        ArgumentCaptor<FeedbackRecord> captor = ArgumentCaptor.forClass(FeedbackRecord.class);
        doAnswer(invocation -> {
            FeedbackRecord r = invocation.getArgument(0);
            r.setId(202L);
            return 1;
        }).when(mapper).insert(captor.capture());

        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("登录用户的反馈内容。", "邮箱 a@b.com", "pages/feedback/feedback", "sess-ignored");

        FeedbackResultVO result = service.submit(req);

        assertEquals(202L, result.id());
        FeedbackRecord saved = captor.getValue();
        assertEquals(99L, saved.getUserId());
        // 登录用户不应保存 sessionId
        assertNull(saved.getSessionId());
    }

    @Test
    void tooShortContentRejected() {
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("短", null, null, "sess-anon");
        assertThrows(BusinessException.class, () -> service.submit(req));
        verify(mapper, never()).insert(any(FeedbackRecord.class));
    }

    @Test
    void tooLongContactRejected() {
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        FeedbackService service = new FeedbackService(mapper, props());
        String longContact = "x".repeat(101);
        FeedbackRequest req = new FeedbackRequest("这是合法长度的反馈内容。", longContact, null, "sess-anon");
        assertThrows(BusinessException.class, () -> service.submit(req));
        verify(mapper, never()).insert(any(FeedbackRecord.class));
    }

    @Test
    void anonymousWithoutSessionIdRejected() {
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("这是合法长度的反馈内容。", null, null, "");
        assertThrows(BusinessException.class, () -> service.submit(req));
        verify(mapper, never()).insert(any(FeedbackRecord.class));
    }

    @Test
    void repeatSubmitSameContentAndContactRejected() {
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        // 命中 60 秒内同身份 + 同 content + 同 contact → recentCount = 1
        when(mapper.selectCount(any())).thenReturn(1L);
        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("这是合法长度的反馈内容。", "联系方式 A", null, "sess-anon");
        BusinessException ex = assertThrows(BusinessException.class, () -> service.submit(req));
        assertEquals(42901, ex.getCode());
        assertEquals("这条反馈刚刚已经提交，请稍后再试", ex.getMessage());
        verify(mapper, never()).insert(any(FeedbackRecord.class));
    }

    @Test
    void differentContentAllowedForSameAnonymousSession() {
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        // 不同 content → recentCount = 0，允许提交
        when(mapper.selectCount(any())).thenReturn(0L);
        ArgumentCaptor<FeedbackRecord> captor = ArgumentCaptor.forClass(FeedbackRecord.class);
        doAnswer(invocation -> {
            FeedbackRecord r = invocation.getArgument(0);
            r.setId(303L);
            return 1;
        }).when(mapper).insert(captor.capture());

        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("这是另一条不同的反馈内容。", null, null, "sess-anon");
        FeedbackResultVO result = service.submit(req);
        assertEquals(303L, result.id());
        FeedbackRecord saved = captor.getValue();
        assertEquals("这是另一条不同的反馈内容。", saved.getContent());
    }

    @Test
    void differentContentAllowedForLoggedInUser() {
        UserContext.set(7L);
        FeedbackRecordMapper mapper = mock(FeedbackRecordMapper.class);
        when(mapper.selectCount(any())).thenReturn(0L);
        ArgumentCaptor<FeedbackRecord> captor = ArgumentCaptor.forClass(FeedbackRecord.class);
        doAnswer(invocation -> {
            FeedbackRecord r = invocation.getArgument(0);
            r.setId(404L);
            return 1;
        }).when(mapper).insert(captor.capture());

        FeedbackService service = new FeedbackService(mapper, props());
        FeedbackRequest req = new FeedbackRequest("登录用户提交新的反馈内容。", null, null, null);
        FeedbackResultVO result = service.submit(req);
        assertEquals(404L, result.id());
        FeedbackRecord saved = captor.getValue();
        assertEquals(7L, saved.getUserId());
        assertNull(saved.getSessionId());
    }
}
