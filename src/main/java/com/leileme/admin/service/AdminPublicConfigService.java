package com.leileme.admin.service;

import com.leileme.admin.dto.AdminPublicConfigUpdateRequest;
import com.leileme.admin.event.PublicConfigUpdatedEvent;
import com.leileme.common.config.PublicConfigProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理后台-公共配置服务。
 * 直接读取/修改 PublicConfigProperties 的字段。
 * 更新后通过 ApplicationEventPublisher 发布事件（小程序端 30 分钟自动过期，缓存清理暂不实现）。
 */
@Service
public class AdminPublicConfigService {
    private final PublicConfigProperties publicConfigProperties;
    private final ApplicationEventPublisher eventPublisher;

    public AdminPublicConfigService(PublicConfigProperties publicConfigProperties,
                                     ApplicationEventPublisher eventPublisher) {
        this.publicConfigProperties = publicConfigProperties;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 读取当前公共配置。
     */
    public PublicConfigProperties get() {
        return publicConfigProperties;
    }

    /**
     * 更新公共配置。
     * 仅更新请求中非 null 的字段，避免误清空。
     */
    @Transactional
    public PublicConfigProperties update(AdminPublicConfigUpdateRequest request) {
        if (request.appName() != null) {
            publicConfigProperties.setAppName(request.appName());
        }
        if (request.slogan() != null) {
            publicConfigProperties.setSlogan(request.slogan());
        }
        if (request.versionName() != null) {
            publicConfigProperties.setVersionName(request.versionName());
        }
        if (request.aboutTitle() != null) {
            publicConfigProperties.setAboutTitle(request.aboutTitle());
        }
        if (request.aboutContent() != null) {
            publicConfigProperties.setAboutContent(request.aboutContent());
        }
        if (request.feedbackEnabled() != null) {
            publicConfigProperties.setFeedbackEnabled(request.feedbackEnabled());
        }
        if (request.feedbackHint() != null) {
            publicConfigProperties.setFeedbackHint(request.feedbackHint());
        }
        if (request.contactText() != null) {
            publicConfigProperties.setContactText(request.contactText());
        }
        if (request.announcement() != null) {
            publicConfigProperties.setAnnouncement(request.announcement());
        }
        // 发布更新事件
        eventPublisher.publishEvent(new PublicConfigUpdatedEvent(
                publicConfigProperties.getAppName(),
                publicConfigProperties.getVersionName()
        ));
        return publicConfigProperties;
    }
}
