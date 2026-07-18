package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.admin.dto.AdminContentSaveRequest;
import com.leileme.admin.dto.AdminContentStatusRequest;
import com.leileme.admin.entity.ContentTagRelation;
import com.leileme.admin.mapper.ContentTagRelationMapper;
import com.leileme.admin.vo.AdminContentVO;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.vo.PageResult;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理后台-内容服务。
 * 内容状态：0=草稿 1=发布 2=下架。发布时设置 publishedAt=now()。
 */
@Service
public class AdminContentService {
    private final ContentMapper contentMapper;
    private final ContentTagRelationMapper contentTagRelationMapper;

    public AdminContentService(ContentMapper contentMapper,
                                ContentTagRelationMapper contentTagRelationMapper) {
        this.contentMapper = contentMapper;
        this.contentTagRelationMapper = contentTagRelationMapper;
    }

    /**
     * 分页查询内容列表。
     */
    public PageResult<AdminContentVO> page(int page, int size,
                                            String keyword, Integer status,
                                            String contentType, Long categoryId) {
        Page<Content> p = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : Math.min(size, 50));
        IPage<Content> result = contentMapper.selectPage(p, Wrappers.lambdaQuery(Content.class)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(Content::getTitle, keyword)
                        .or().like(Content::getSubtitle, keyword)
                        .or().like(Content::getSummary, keyword)
                        .or().like(Content::getSearchKeywords, keyword)
                        .or().like(Content::getAuthorName, keyword))
                .eq(status != null, Content::getStatus, status)
                .eq(contentType != null && !contentType.isBlank(), Content::getContentType, contentType)
                .eq(categoryId != null, Content::getCategoryId, categoryId)
                .orderByDesc(Content::getCreatedAt));
        List<AdminContentVO> list = result.getRecords().stream()
                .map(this::toVO)
                .toList();
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(),
                result.getCurrent() < result.getPages(), list);
    }

    /**
     * 查询内容详情（含标签列表）。
     */
    public AdminContentVO detail(Long id) {
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException(40401, "内容不存在");
        }
        return toVO(content);
    }

    /**
     * 新建内容（草稿）。同时保存标签关联。
     */
    @Transactional
    public AdminContentVO create(AdminContentSaveRequest request) {
        Content content = new Content();
        applyToEntity(content, request);
        content.setViewCount(0L);
        content.setFavoriteCount(0L);
        content.setShareCount(0L);
        // 发布时设置 publishedAt
        if (request.status() != null && request.status() == 1) {
            content.setPublishedAt(LocalDateTime.now());
        }
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        contentMapper.insert(content);
        saveTagRelations(content.getId(), request.tagIds());
        return toVO(content);
    }

    /**
     * 更新内容。同时重置标签关联。
     */
    @Transactional
    public AdminContentVO update(Long id, AdminContentSaveRequest request) {
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException(40401, "内容不存在");
        }
        applyToEntity(content, request);
        // 由草稿/下架切换为发布时，若 publishedAt 为空则设置
        if (request.status() != null && request.status() == 1 && content.getPublishedAt() == null) {
            content.setPublishedAt(LocalDateTime.now());
        }
        content.setUpdatedAt(LocalDateTime.now());
        contentMapper.updateById(content);
        saveTagRelations(content.getId(), request.tagIds());
        return toVO(content);
    }

    /**
     * 更新内容状态。发布时设置 publishedAt。
     */
    @Transactional
    public AdminContentVO updateStatus(Long id, AdminContentStatusRequest request) {
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException(40401, "内容不存在");
        }
        if (request.status() == null || request.status() < 0 || request.status() > 2) {
            throw new BusinessException(40001, "状态值非法");
        }
        content.setStatus(request.status());
        if (request.status() == 1 && content.getPublishedAt() == null) {
            content.setPublishedAt(LocalDateTime.now());
        }
        content.setUpdatedAt(LocalDateTime.now());
        contentMapper.updateById(content);
        return toVO(content);
    }

    /**
     * 删除内容。同时删除标签关联。
     */
    @Transactional
    public void delete(Long id) {
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException(40401, "内容不存在");
        }
        contentMapper.deleteById(id);
        contentTagRelationMapper.delete(Wrappers.lambdaQuery(ContentTagRelation.class)
                .eq(ContentTagRelation::getContentId, id));
    }

    /**
     * 将请求字段应用到实体上（不处理 id、viewCount 等系统字段）。
     */
    private void applyToEntity(Content content, AdminContentSaveRequest request) {
        content.setTitle(request.title());
        content.setSubtitle(request.subtitle());
        content.setSummary(request.summary());
        content.setBody(request.body());
        content.setSearchKeywords(request.searchKeywords());
        content.setCoverUrl(request.coverUrl());
        content.setSourceName(request.sourceName());
        content.setSourceUrl(request.sourceUrl());
        content.setAuthorName(request.authorName());
        content.setContentType(request.contentType());
        content.setCategoryId(request.categoryId());
        content.setStatus(request.status());
        content.setIsOriginal(request.isOriginal());
        content.setIsTop(request.isTop());
        content.setHotScore(request.hotScore());
    }

    /**
     * 保存内容-标签关联：先删除旧关联再插入新关联。
     */
    private void saveTagRelations(Long contentId, List<Long> tagIds) {
        contentTagRelationMapper.delete(Wrappers.lambdaQuery(ContentTagRelation.class)
                .eq(ContentTagRelation::getContentId, contentId));
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<Long> distinct = tagIds.stream().distinct().toList();
        for (Long tagId : distinct) {
            ContentTagRelation rel = new ContentTagRelation();
            rel.setContentId(contentId);
            rel.setTagId(tagId);
            rel.setCreatedAt(now);
            contentTagRelationMapper.insert(rel);
        }
    }

    /**
     * 转换为 VO（含 tagIds）。
     */
    private AdminContentVO toVO(Content content) {
        List<Long> tagIds = contentTagRelationMapper.selectList(
                        Wrappers.lambdaQuery(ContentTagRelation.class)
                                .eq(ContentTagRelation::getContentId, content.getId()))
                .stream().map(ContentTagRelation::getTagId).toList();
        return new AdminContentVO(
                content.getId(),
                content.getTitle(),
                content.getSubtitle(),
                content.getSummary(),
                content.getBody(),
                content.getSearchKeywords(),
                content.getCoverUrl(),
                content.getSourceName(),
                content.getSourceUrl(),
                content.getAuthorName(),
                content.getContentType(),
                content.getCategoryId(),
                content.getStatus(),
                content.getIsOriginal(),
                content.getIsTop(),
                content.getHotScore(),
                content.getViewCount(),
                content.getFavoriteCount(),
                content.getShareCount(),
                content.getPublishedAt(),
                content.getCreatedAt(),
                content.getUpdatedAt(),
                new ArrayList<>(tagIds)
        );
    }
}
