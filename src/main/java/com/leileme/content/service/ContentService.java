package com.leileme.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.common.auth.UserContext;
import com.leileme.common.exception.BusinessException;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.content.vo.ContentDetailVO;
import com.leileme.content.vo.RandomTipVO;
import com.leileme.history.service.HistoryService;
import com.leileme.user.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContentService {
    private final ContentMapper contentMapper;
    private final ContentAssembler assembler;
    private final HistoryService historyService;
    private final FavoriteService favoriteService;

    public ContentService(ContentMapper contentMapper, ContentAssembler assembler,
                          HistoryService historyService, FavoriteService favoriteService) {
        this.contentMapper = contentMapper;
        this.assembler = assembler;
        this.historyService = historyService;
        this.favoriteService = favoriteService;
    }

    public ContentDetailVO getDetail(Long id, Long userId, String sessionId) {
        Content content = contentMapper.selectById(id);
        if (content == null || content.getStatus() == null || content.getStatus() != 1) {
            throw new BusinessException(40401, "内容不存在");
        }

        // 登录用户优先用 UserContext 中的 userId
        Long currentUserId = UserContext.get();
        Long effectiveUserId = currentUserId != null ? currentUserId : userId;

        contentMapper.incrementViewCount(id);
        historyService.recordBrowse(content.getId(), effectiveUserId, sessionId);

        // 登录用户返回真实收藏状态
        boolean isFavorite = favoriteService.isFavorited(effectiveUserId, content.getId());

        // 相关推荐也带上收藏状态
        List<Content> relatedEntities = contentMapper.selectRelated(content.getCategoryId(), content.getId(), 4);
        Set<Long> relatedFavoritedIds = favoriteService.getFavoritedContentIds(effectiveUserId,
                relatedEntities.stream().map(Content::getId).toList());
        List<ContentCardVO> related = relatedEntities.stream()
                .map(c -> assembler.fromEntity(c, relatedFavoritedIds))
                .toList();

        return new ContentDetailVO(
                content.getId(), content.getContentType(), content.getTitle(), content.getSummary(), content.getBody(),
                content.getCoverUrl(), content.getSourceName(), content.getSourceUrl(), content.getAuthorName(),
                contentMapper.selectTagNames(content.getId()), content.getViewCount() + 1,
                content.getFavoriteCount(), content.getShareCount(), content.getPublishedAt(), isFavorite, related
        );
    }

    public RandomTipVO randomTip() {
        long count = contentMapper.selectCount(Wrappers.lambdaQuery(Content.class)
                .eq(Content::getStatus, 1)
                .eq(Content::getContentType, "RANDOM_TIP"));
        if (count == 0) {
            throw new BusinessException(40401, "暂时没有随机建议");
        }
        long offset = ThreadLocalRandom.current().nextLong(count);
        Page<Content> page = new Page<>(offset + 1, 1, false);
        List<Content> rows = contentMapper.selectPage(page, Wrappers.lambdaQuery(Content.class)
                        .eq(Content::getStatus, 1)
                        .eq(Content::getContentType, "RANDOM_TIP")
                        .orderByAsc(Content::getId))
                .getRecords();
        Content item = rows.getFirst();
        return new RandomTipVO(item.getId(), item.getTitle(), item.getSummary());
    }
}
