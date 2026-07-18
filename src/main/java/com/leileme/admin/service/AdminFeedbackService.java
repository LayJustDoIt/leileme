package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.admin.config.AdminUserContext;
import com.leileme.admin.dto.AdminFeedbackNoteRequest;
import com.leileme.admin.dto.AdminFeedbackStatusRequest;
import com.leileme.admin.vo.AdminFeedbackVO;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.vo.PageResult;
import com.leileme.feedback.entity.FeedbackRecord;
import com.leileme.feedback.mapper.FeedbackRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台-反馈记录服务。
 * status：PENDING/PROCESSED/IGNORED。
 * 更新状态/备注时同时记录 handledBy 和 handledAt。
 */
@Service
public class AdminFeedbackService {
    private final FeedbackRecordMapper feedbackMapper;

    public AdminFeedbackService(FeedbackRecordMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    /**
     * 分页查询反馈列表。
     */
    public PageResult<AdminFeedbackVO> page(int page, int size, String keyword, String status) {
        Page<FeedbackRecord> p = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : Math.min(size, 50));
        IPage<FeedbackRecord> result = feedbackMapper.selectPage(p, Wrappers.lambdaQuery(FeedbackRecord.class)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(FeedbackRecord::getContent, keyword)
                        .or().like(FeedbackRecord::getContact, keyword)
                        .or().like(FeedbackRecord::getPagePath, keyword)
                        .or().like(FeedbackRecord::getSessionId, keyword))
                .eq(status != null && !status.isBlank(), FeedbackRecord::getStatus, status)
                .orderByDesc(FeedbackRecord::getCreatedAt));
        List<AdminFeedbackVO> list = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(),
                result.getCurrent() < result.getPages(), list);
    }

    /**
     * 反馈详情。
     */
    public AdminFeedbackVO detail(Long id) {
        FeedbackRecord record = feedbackMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(40401, "反馈记录不存在");
        }
        return toVO(record);
    }

    /**
     * 更新处理状态。同时记录 handledBy 和 handledAt。
     */
    @Transactional
    public AdminFeedbackVO updateStatus(Long id, AdminFeedbackStatusRequest request) {
        FeedbackRecord record = feedbackMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(40401, "反馈记录不存在");
        }
        record.setStatus(request.status());
        record.setHandledBy(AdminUserContext.getAdminId());
        record.setHandledAt(LocalDateTime.now());
        feedbackMapper.updateById(record);
        return toVO(record);
    }

    /**
     * 更新管理员备注。同时记录 handledBy 和 handledAt。
     */
    @Transactional
    public AdminFeedbackVO updateNote(Long id, AdminFeedbackNoteRequest request) {
        FeedbackRecord record = feedbackMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(40401, "反馈记录不存在");
        }
        record.setAdminNote(request.adminNote());
        record.setHandledBy(AdminUserContext.getAdminId());
        record.setHandledAt(LocalDateTime.now());
        feedbackMapper.updateById(record);
        return toVO(record);
    }

    /**
     * 转换为 VO。
     */
    private AdminFeedbackVO toVO(FeedbackRecord record) {
        return new AdminFeedbackVO(
                record.getId(),
                record.getUserId(),
                record.getSessionId(),
                record.getContent(),
                record.getContact(),
                record.getPagePath(),
                record.getSubmittedAt(),
                record.getCreatedAt(),
                record.getStatus(),
                record.getAdminNote(),
                record.getHandledBy(),
                record.getHandledAt()
        );
    }
}
