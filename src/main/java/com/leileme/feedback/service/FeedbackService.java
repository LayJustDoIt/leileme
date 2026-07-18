package com.leileme.feedback.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.common.auth.UserContext;
import com.leileme.common.exception.BusinessException;
import com.leileme.feedback.config.FeedbackProperties;
import com.leileme.feedback.dto.FeedbackRequest;
import com.leileme.feedback.entity.FeedbackRecord;
import com.leileme.feedback.mapper.FeedbackRecordMapper;
import com.leileme.feedback.vo.FeedbackResultVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 反馈记录服务：
 * - 登录用户保存 userId；未登录保存 sessionId
 * - content 5-1000 字、contact 最多 100 字（@Valid 已校验，业务再做一层防御）
 * - 防重复：同一身份 + 完全相同的 content + 完全相同的 contact + 60 秒内重复提交
 * - 不同反馈内容允许连续提交
 * - 不返回用户隐私
 */
@Service
public class FeedbackService {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final FeedbackRecordMapper feedbackMapper;
    private final FeedbackProperties properties;

    public FeedbackService(FeedbackRecordMapper feedbackMapper, FeedbackProperties properties) {
        this.feedbackMapper = feedbackMapper;
        this.properties = properties;
    }

    public FeedbackResultVO submit(FeedbackRequest request) {
        // 防御性二次校验
        String content = request.content() == null ? "" : request.content().trim();
        if (content.length() < 5 || content.length() > 1000) {
            throw new BusinessException(40001, "反馈内容长度需在 5 到 1000 字之间");
        }
        String contact = request.contact() == null ? null : request.contact().trim();
        if (contact != null && contact.length() > 100) {
            throw new BusinessException(40001, "联系方式最长 100 字");
        }
        // contact 空串统一为 null，便于去重匹配
        if (contact != null && contact.isEmpty()) {
            contact = null;
        }

        Long userId = UserContext.get();
        String sessionId = request.sessionId();
        // 未登录用户必须提供 sessionId，否则无法标识匿名身份
        if (userId == null && (sessionId == null || sessionId.isBlank())) {
            throw new BusinessException(40001, "缺少会话标识");
        }

        // 防重复：同一身份 + 完全相同 content + 完全相同 contact + 60 秒内
        // 注意：MyBatis-Plus 的 .eq(field, null) 会生成 "field = null"（SQL 中永远 false），
        // 必须用 .isNull(field) 处理 null 值，否则去重永远查不到记录。
        LocalDateTime minSubmittedAt = LocalDateTime.now().minusSeconds(properties.getMinIntervalSeconds());
        Long recentCount;
        if (userId != null) {
            var wrapper = Wrappers.lambdaQuery(FeedbackRecord.class)
                    .eq(FeedbackRecord::getUserId, userId)
                    .eq(FeedbackRecord::getContent, content)
                    .ge(FeedbackRecord::getSubmittedAt, minSubmittedAt);
            if (contact != null) {
                wrapper.eq(FeedbackRecord::getContact, contact);
            } else {
                wrapper.isNull(FeedbackRecord::getContact);
            }
            recentCount = feedbackMapper.selectCount(wrapper);
        } else {
            var wrapper = Wrappers.lambdaQuery(FeedbackRecord.class)
                    .isNull(FeedbackRecord::getUserId)
                    .eq(FeedbackRecord::getSessionId, sessionId)
                    .eq(FeedbackRecord::getContent, content)
                    .ge(FeedbackRecord::getSubmittedAt, minSubmittedAt);
            if (contact != null) {
                wrapper.eq(FeedbackRecord::getContact, contact);
            } else {
                wrapper.isNull(FeedbackRecord::getContact);
            }
            recentCount = feedbackMapper.selectCount(wrapper);
        }
        if (recentCount != null && recentCount > 0) {
            throw new BusinessException(42901, "这条反馈刚刚已经提交，请稍后再试");
        }

        FeedbackRecord record = new FeedbackRecord();
        record.setUserId(userId);
        record.setSessionId(userId == null ? sessionId : null);
        record.setContent(content);
        record.setContact(contact);
        record.setPagePath(request.pagePath());
        record.setSubmittedAt(LocalDateTime.now());
        feedbackMapper.insert(record);

        return new FeedbackResultVO(
                record.getId() == null ? 0L : record.getId(),
                ISO.format(record.getSubmittedAt())
        );
    }
}
