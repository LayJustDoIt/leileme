package com.leileme.auth.service;

import com.leileme.auth.dto.WechatLoginRequest;
import com.leileme.auth.vo.LoginResponse;
import com.leileme.common.auth.AuthProperties;
import com.leileme.common.auth.JwtUtil;
import com.leileme.common.auth.UserContext;
import com.leileme.history.service.HistoryService;
import com.leileme.user.entity.UserAccount;
import com.leileme.user.mapper.UserAccountMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 登录服务测试：
 * - mock 登录首次创建用户
 * - mock 登录重复使用同一 code 返回同一用户
 */
class AuthServiceTest {

    private AuthProperties mockEnabledProps() {
        AuthProperties props = new AuthProperties();
        props.getWechat().setMockEnabled(true);
        props.getJwt().setSecret("test-jwt-secret-at-least-32-bytes-long!!!");
        return props;
    }

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void mockLoginFirstTimeCreatesUser() {
        UserAccountMapper userAccountMapper = mock(UserAccountMapper.class);
        HistoryService historyService = mock(HistoryService.class);
        AuthProperties props = mockEnabledProps();
        JwtUtil jwtUtil = new JwtUtil(props);
        AuthService service = new AuthService(props, jwtUtil, userAccountMapper, historyService);

        // 首次登录：selectOne 返回 null
        when(userAccountMapper.selectOne(any())).thenReturn(null);
        // 模拟 insert 时自增 ID 赋值
        doAnswer(invocation -> {
            UserAccount u = invocation.getArgument(0);
            u.setId(1L);
            return 1;
        }).when(userAccountMapper).insert(any(UserAccount.class));

        LoginResponse resp = service.login(new WechatLoginRequest("dev-user-001", "sess-anon-1"));

        assertNotNull(resp.accessToken());
        assertFalse(resp.accessToken().isBlank());
        assertEquals(1L, resp.user().id());
        assertNull(resp.user().nickname());

        // 验证创建了用户，且 openid 为 mock-dev-user-001
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountMapper).insert(captor.capture());
        assertEquals("mock-dev-user-001", captor.getValue().getOpenid());

        // 验证合并了匿名数据
        verify(historyService).mergeAnonymousData(eq(1L), eq("sess-anon-1"));
    }

    @Test
    void mockLoginRepeatSameCodeReturnsSameUser() {
        UserAccountMapper userAccountMapper = mock(UserAccountMapper.class);
        HistoryService historyService = mock(HistoryService.class);
        AuthProperties props = mockEnabledProps();
        JwtUtil jwtUtil = new JwtUtil(props);
        AuthService service = new AuthService(props, jwtUtil, userAccountMapper, historyService);

        // 已存在用户（与首次登录 code 相同，对应同一 mock openid）
        UserAccount existing = new UserAccount();
        existing.setId(99L);
        existing.setOpenid("mock-dev-user-001");
        existing.setStatus(1);
        when(userAccountMapper.selectOne(any())).thenReturn(existing);

        LoginResponse resp = service.login(new WechatLoginRequest("dev-user-001", null));

        // 返回同一用户
        assertEquals(99L, resp.user().id());
        // 不应再次插入
        verify(userAccountMapper, never()).insert(any(UserAccount.class));
        // 应更新 last_login_at
        verify(userAccountMapper).updateById(existing);
        // sessionId 为空，不合并
        verify(historyService, never()).mergeAnonymousData(any(), any());
    }
}
