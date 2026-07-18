package com.leileme.admin.vo;

/**
 * 管理后台-基础数据概览。
 */
public record DashboardSummaryVO(
        long contentTotal,
        long contentPublished,
        long contentDraft,
        long categoryTotal,
        long tagTotal,
        long feedbackPending,
        long feedbackTotal,
        long todayContentCount,
        long todayFeedbackCount
) {}
