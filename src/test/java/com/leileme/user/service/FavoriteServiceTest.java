package com.leileme.user.service;

import com.leileme.common.auth.UserContext;
import com.leileme.common.exception.UnauthorizedException;
import com.leileme.content.controller.FavoriteController;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.service.ContentAssembler;
import com.leileme.user.entity.UserFavorite;
import com.leileme.user.mapper.UserFavoriteMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 收藏服务测试：
 * - 无 Token 收藏返回 401（控制器层）
 * - 收藏幂等
 * - 取消收藏幂等
 * - 收藏计数正确（新建收藏时计数 +1）
 */
class FavoriteServiceTest {

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    private Content publishedContent() {
        Content c = new Content();
        c.setId(1001L);
        c.setStatus(1);
        c.setFavoriteCount(5L);
        return c;
    }

    @Test
    void favoriteWithoutTokenThrows401() {
        FavoriteService favoriteService = mock(FavoriteService.class);
        FavoriteController controller = new FavoriteController(favoriteService);

        // 未设置 UserContext（模拟无 Token 请求）
        assertThrows(UnauthorizedException.class, () -> controller.favorite(1001L));
        verify(favoriteService, never()).favorite(anyLong(), anyLong());
    }

    @Test
    void favoriteIsIdempotent() {
        UserFavoriteMapper favMapper = mock(UserFavoriteMapper.class);
        ContentMapper contentMapper = mock(ContentMapper.class);
        ContentAssembler assembler = new ContentAssembler(contentMapper);
        FavoriteService service = new FavoriteService(favMapper, contentMapper, assembler);

        when(contentMapper.selectById(1001L)).thenReturn(publishedContent());
        // 已存在 1 条收藏 → 幂等：不应再次插入或增加计数
        when(favMapper.selectCount(any())).thenReturn(1L);

        service.favorite(1L, 1001L);

        verify(favMapper, never()).insert(any(UserFavorite.class));
        verify(contentMapper, never()).incrementFavoriteCount(1001L);
    }

    @Test
    void unfavoriteIsIdempotent() {
        UserFavoriteMapper favMapper = mock(UserFavoriteMapper.class);
        ContentMapper contentMapper = mock(ContentMapper.class);
        ContentAssembler assembler = new ContentAssembler(contentMapper);
        FavoriteService service = new FavoriteService(favMapper, contentMapper, assembler);

        // 删除返回 0（不存在）→ 不应调用减计数
        when(favMapper.delete(any())).thenReturn(0);

        service.unfavorite(1L, 1001L);

        verify(contentMapper, never()).decrementFavoriteCount(1001L);
    }

    @Test
    void favoriteCountIncrementedOnNewFavorite() {
        UserFavoriteMapper favMapper = mock(UserFavoriteMapper.class);
        ContentMapper contentMapper = mock(ContentMapper.class);
        ContentAssembler assembler = new ContentAssembler(contentMapper);
        FavoriteService service = new FavoriteService(favMapper, contentMapper, assembler);

        when(contentMapper.selectById(1001L)).thenReturn(publishedContent());
        // 未收藏
        when(favMapper.selectCount(any())).thenReturn(0L);

        service.favorite(1L, 1001L);

        verify(favMapper).insert(any(UserFavorite.class));
        verify(contentMapper).incrementFavoriteCount(1001L);
    }
}
