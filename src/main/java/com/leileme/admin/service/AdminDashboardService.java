package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.admin.entity.ContentTag;
import com.leileme.admin.mapper.ContentTagMapper;
import com.leileme.admin.vo.DashboardSummaryVO;
import com.leileme.category.entity.ContentCategory;
import com.leileme.category.mapper.ContentCategoryMapper;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.feedback.entity.FeedbackRecord;
import com.leileme.feedback.mapper.FeedbackRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理后台-基础数据概览服务。
 * 使用各模块 Mapper 查询 count。
 */
@Service
public class AdminDashboardService {
    private final ContentMapper contentMapper;
    private final ContentCategoryMapper categoryMapper;
    private final ContentTagMapper tagMapper;
    private final FeedbackRecordMapper feedbackMapper;

    public AdminDashboardService(ContentMapper contentMapper,
                                  ContentCategoryMapper categoryMapper,
                                  ContentTagMapper tagMapper,
                                  FeedbackRecordMapper feedbackMapper) {
        this.contentMapper = contentMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.feedbackMapper = feedbackMapper;
    }

    /**
     * 返回基础数据概览。
     */
    public DashboardSummaryVO summary() {
        long contentTotal = countOf(contentMapper.selectCount(null));
        long contentPublished = countOf(contentMapper.selectCount(
                Wrappers.lambdaQuery(Content.class).eq(Content::getStatus, 1)));
        long contentDraft = countOf(contentMapper.selectCount(
                Wrappers.lambdaQuery(Content.class).eq(Content::getStatus, 0)));
        long categoryTotal = countOf(categoryMapper.selectCount(
                Wrappers.lambdaQuery(ContentCategory.class).eq(ContentCategory::getStatus, 1)));
        long tagTotal = countOf(tagMapper.selectCount(
                Wrappers.lambdaQuery(ContentTag.class).eq(ContentTag::getStatus, 1)));
        long feedbackPending = countOf(feedbackMapper.selectCount(
                Wrappers.lambdaQuery(FeedbackRecord.class)
                        .isNull(FeedbackRecord::getStatus)
                        .or().eq(FeedbackRecord::getStatus, "PENDING")));
        long feedbackTotal = countOf(feedbackMapper.selectCount(null));

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayContentCount = countOf(contentMapper.selectCount(
                Wrappers.lambdaQuery(Content.class).ge(Content::getCreatedAt, todayStart)));
        long todayFeedbackCount = countOf(feedbackMapper.selectCount(
                Wrappers.lambdaQuery(FeedbackRecord.class).ge(FeedbackRecord::getCreatedAt, todayStart)));

        return new DashboardSummaryVO(
                contentTotal,
                contentPublished,
                contentDraft,
                categoryTotal,
                tagTotal,
                feedbackPending,
                feedbackTotal,
                todayContentCount,
                todayFeedbackCount
        );
    }

    /**
     * MyBatis-Plus selectCount 返回 Long，但可能为 null。
     */
    private long countOf(Long count) {
        return count == null ? 0L : count;
    }
}
