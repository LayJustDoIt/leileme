package com.leileme.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.vo.PageResult;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.content.service.ContentAssembler;
import com.leileme.user.entity.UserFavorite;
import com.leileme.user.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final UserFavoriteMapper userFavoriteMapper;
    private final ContentMapper contentMapper;
    private final ContentAssembler assembler;

    public FavoriteService(UserFavoriteMapper userFavoriteMapper,
                           ContentMapper contentMapper,
                           ContentAssembler assembler) {
        this.userFavoriteMapper = userFavoriteMapper;
        this.contentMapper = contentMapper;
        this.assembler = assembler;
    }

    /**
     * 收藏。幂等：已存在则不重复插入，计数也不重复增加。
     * 收藏与计数更新在同一事务内。
     */
    @Transactional
    public void favorite(Long userId, Long contentId) {
        Content content = contentMapper.selectById(contentId);
        if (content == null || content.getStatus() == null || content.getStatus() != 1) {
            throw new BusinessException(40401, "内容不存在");
        }

        Long count = userFavoriteMapper.selectCount(Wrappers.lambdaQuery(UserFavorite.class)
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getContentId, contentId));
        if (count > 0) {
            return; // 幂等：已收藏
        }

        UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setContentId(contentId);
        userFavoriteMapper.insert(fav);
        contentMapper.incrementFavoriteCount(contentId);
    }

    /**
     * 取消收藏。幂等：不存在也返回成功，计数不会减为负。
     */
    @Transactional
    public void unfavorite(Long userId, Long contentId) {
        int deleted = userFavoriteMapper.delete(Wrappers.lambdaQuery(UserFavorite.class)
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getContentId, contentId));
        if (deleted > 0) {
            contentMapper.decrementFavoriteCount(contentId);
        }
    }

    /**
     * 判断用户是否已收藏某内容。
     */
    public boolean isFavorited(Long userId, Long contentId) {
        if (userId == null) return false;
        Long count = userFavoriteMapper.selectCount(Wrappers.lambdaQuery(UserFavorite.class)
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getContentId, contentId));
        return count > 0;
    }

    /**
     * 批量查询用户已收藏的内容 ID。
     */
    public Set<Long> getFavoritedContentIds(Long userId, List<Long> contentIds) {
        if (userId == null || contentIds == null || contentIds.isEmpty()) return Set.of();
        List<UserFavorite> favorites = userFavoriteMapper.selectList(Wrappers.lambdaQuery(UserFavorite.class)
                .eq(UserFavorite::getUserId, userId)
                .in(UserFavorite::getContentId, contentIds));
        return favorites.stream().map(UserFavorite::getContentId).collect(Collectors.toSet());
    }

    /**
     * 分页查询收藏列表，按收藏时间倒序。
     */
    public PageResult<ContentCardVO> listFavorites(Long userId, long page, long size) {
        Page<UserFavorite> p = new Page<>(page, size);
        Page<UserFavorite> result = userFavoriteMapper.selectPage(p,
                Wrappers.lambdaQuery(UserFavorite.class)
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreatedAt));

        List<UserFavorite> favorites = result.getRecords();
        if (favorites.isEmpty()) {
            return new PageResult<>(page, size, result.getTotal(), result.hasNext(), List.of());
        }

        List<Long> contentIds = favorites.stream().map(UserFavorite::getContentId).toList();
        List<Content> contents = contentMapper.selectBatchIds(contentIds);

        // 按 favorites 顺序排序
        List<ContentCardVO> list = favorites.stream()
                .map(fav -> contents.stream().filter(c -> c.getId().equals(fav.getContentId())).findFirst().orElse(null))
                .filter(java.util.Objects::nonNull)
                .filter(c -> c.getStatus() != null && c.getStatus() == 1)
                .map(c -> assembler.fromEntity(c, true))
                .toList();

        return new PageResult<>(page, size, result.getTotal(), result.hasNext(), list);
    }
}
