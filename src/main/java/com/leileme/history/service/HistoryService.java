package com.leileme.history.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.common.vo.PageResult;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.history.entity.UserBrowseRecord;
import com.leileme.history.mapper.UserBrowseRecordMapper;
import com.leileme.history.vo.HistoryItemVO;
import com.leileme.user.entity.UserFavorite;
import com.leileme.user.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryService {
    private final UserBrowseRecordMapper browseRecordMapper;
    private final ContentMapper contentMapper;
    private final UserFavoriteMapper userFavoriteMapper;

    public HistoryService(UserBrowseRecordMapper browseRecordMapper,
                          ContentMapper contentMapper,
                          UserFavoriteMapper userFavoriteMapper) {
        this.browseRecordMapper = browseRecordMapper;
        this.contentMapper = contentMapper;
        this.userFavoriteMapper = userFavoriteMapper;
    }

    /**
     * 记录浏览。同一用户/会话重复浏览同一内容时累加 browse_count，更新 last_browsed_at。
     */
    @Transactional
    public void recordBrowse(Long contentId, Long userId, String sessionId) {
        LocalDateTime now = LocalDateTime.now();
        if (userId != null) {
            UserBrowseRecord existing = browseRecordMapper.selectOne(
                    Wrappers.lambdaQuery(UserBrowseRecord.class)
                            .eq(UserBrowseRecord::getUserId, userId)
                            .eq(UserBrowseRecord::getContentId, contentId)
                            .last("LIMIT 1"));
            if (existing != null) {
                existing.setBrowseCount(existing.getBrowseCount() + 1);
                existing.setLastBrowsedAt(now);
                browseRecordMapper.updateById(existing);
                return;
            }
        }
        if (sessionId != null && !sessionId.isBlank()) {
            UserBrowseRecord existing = browseRecordMapper.selectOne(
                    Wrappers.lambdaQuery(UserBrowseRecord.class)
                            .isNull(UserBrowseRecord::getUserId)
                            .eq(UserBrowseRecord::getSessionId, sessionId)
                            .eq(UserBrowseRecord::getContentId, contentId)
                            .last("LIMIT 1"));
            if (existing != null) {
                existing.setBrowseCount(existing.getBrowseCount() + 1);
                existing.setLastBrowsedAt(now);
                browseRecordMapper.updateById(existing);
                return;
            }
        }
        UserBrowseRecord record = new UserBrowseRecord();
        record.setUserId(userId);
        record.setSessionId(userId != null ? null : sessionId);
        record.setContentId(contentId);
        record.setBrowseCount(1);
        record.setFirstBrowsedAt(now);
        record.setLastBrowsedAt(now);
        browseRecordMapper.insert(record);
    }

    /**
     * 将匿名 sessionId 对应的浏览记录合并到当前 userId。
     * 相同内容合并 browse_count，last_browsed_at 取较新值；合并后避免重复迁移。
     */
    @Transactional
    public void mergeAnonymousData(Long userId, String sessionId) {
        if (userId == null || sessionId == null || sessionId.isBlank()) return;

        List<UserBrowseRecord> sessionRecords = browseRecordMapper.selectList(
                Wrappers.lambdaQuery(UserBrowseRecord.class)
                        .isNull(UserBrowseRecord::getUserId)
                        .eq(UserBrowseRecord::getSessionId, sessionId));
        if (sessionRecords.isEmpty()) return;

        for (UserBrowseRecord sessionRec : sessionRecords) {
            UserBrowseRecord userExisting = browseRecordMapper.selectOne(
                    Wrappers.lambdaQuery(UserBrowseRecord.class)
                            .eq(UserBrowseRecord::getUserId, userId)
                            .eq(UserBrowseRecord::getContentId, sessionRec.getContentId())
                            .last("LIMIT 1"));
            if (userExisting != null) {
                userExisting.setBrowseCount(userExisting.getBrowseCount() + sessionRec.getBrowseCount());
                userExisting.setLastBrowsedAt(
                        userExisting.getLastBrowsedAt().isAfter(sessionRec.getLastBrowsedAt())
                                ? userExisting.getLastBrowsedAt() : sessionRec.getLastBrowsedAt());
                userExisting.setFirstBrowsedAt(
                        userExisting.getFirstBrowsedAt().isBefore(sessionRec.getFirstBrowsedAt())
                                ? userExisting.getFirstBrowsedAt() : sessionRec.getFirstBrowsedAt());
                browseRecordMapper.updateById(userExisting);
                browseRecordMapper.deleteById(sessionRec.getId());
            } else {
                sessionRec.setUserId(userId);
                sessionRec.setSessionId(null);
                browseRecordMapper.updateById(sessionRec);
            }
        }
    }

    /**
     * 分页查询浏览历史，按 last_browsed_at 倒序。
     */
    public PageResult<HistoryItemVO> listHistory(Long userId, long page, long size) {
        Page<UserBrowseRecord> p = new Page<>(page, size);
        Page<UserBrowseRecord> result = browseRecordMapper.selectPage(p,
                Wrappers.lambdaQuery(UserBrowseRecord.class)
                        .eq(UserBrowseRecord::getUserId, userId)
                        .orderByDesc(UserBrowseRecord::getLastBrowsedAt));

        List<UserBrowseRecord> records = result.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(page, size, result.getTotal(), result.hasNext(), List.of());
        }

        List<Long> contentIds = records.stream().map(UserBrowseRecord::getContentId).toList();
        List<Content> contents = contentMapper.selectBatchIds(contentIds);
        Map<Long, Content> contentMap = contents.stream()
                .collect(Collectors.toMap(Content::getId, c -> c, (a, b) -> a));

        // 查询用户对这些内容的收藏状态
        List<UserFavorite> favorites = userFavoriteMapper.selectList(
                Wrappers.lambdaQuery(UserFavorite.class)
                        .eq(UserFavorite::getUserId, userId)
                        .in(UserFavorite::getContentId, contentIds));
        Set<Long> favoritedIds = favorites.stream().map(UserFavorite::getContentId).collect(Collectors.toSet());

        List<HistoryItemVO> list = records.stream()
                .filter(r -> contentMap.containsKey(r.getContentId()))
                .map(r -> {
                    Content c = contentMap.get(r.getContentId());
                    return new HistoryItemVO(
                            c.getId(), c.getContentType(), c.getTitle(), c.getSummary(),
                            c.getCoverUrl(), c.getSourceName(), contentMapper.selectTagNames(c.getId()),
                            c.getViewCount(), c.getFavoriteCount(), c.getPublishedAt(),
                            favoritedIds.contains(c.getId()),
                            r.getBrowseCount(), r.getLastBrowsedAt());
                })
                .toList();

        return new PageResult<>(page, size, result.getTotal(), result.hasNext(), list);
    }

    /**
     * 删除单条浏览历史。
     */
    @Transactional
    public void deleteOne(Long userId, Long contentId) {
        browseRecordMapper.delete(Wrappers.lambdaQuery(UserBrowseRecord.class)
                .eq(UserBrowseRecord::getUserId, userId)
                .eq(UserBrowseRecord::getContentId, contentId));
    }

    /**
     * 清空浏览历史。
     */
    @Transactional
    public void clearAll(Long userId) {
        browseRecordMapper.delete(Wrappers.lambdaQuery(UserBrowseRecord.class)
                .eq(UserBrowseRecord::getUserId, userId));
    }
}
