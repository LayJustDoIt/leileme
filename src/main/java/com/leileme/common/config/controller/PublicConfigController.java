package com.leileme.common.config.controller;

import com.leileme.common.config.PublicConfigProperties;
import com.leileme.common.config.vo.PublicConfigVO;
import com.leileme.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共配置接口：对外可读、不要求登录、不返回任何密钥。
 */
@RestController
@RequestMapping("/api/v1/public-config")
public class PublicConfigController {
    private final PublicConfigProperties properties;

    public PublicConfigController(PublicConfigProperties properties) {
        this.properties = properties;
    }

    @GetMapping
    public ApiResponse<PublicConfigVO> get() {
        // announcement 空串统一对外表现为 null
        String announcement = properties.getAnnouncement();
        if (announcement != null && announcement.isBlank()) {
            announcement = null;
        }
        PublicConfigVO vo = new PublicConfigVO(
                properties.getAppName(),
                properties.getSlogan(),
                properties.getVersionName(),
                properties.getAboutTitle(),
                properties.getAboutContent(),
                properties.isFeedbackEnabled(),
                properties.getFeedbackHint(),
                properties.getContactText(),
                announcement
        );
        return ApiResponse.success(vo);
    }
}
