package com.leileme.feedback.controller;

import com.leileme.common.response.ApiResponse;
import com.leileme.feedback.dto.FeedbackRequest;
import com.leileme.feedback.service.FeedbackService;
import com.leileme.feedback.vo.FeedbackResultVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 反馈接口：登录与匿名用户均可提交。
 */
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ApiResponse<FeedbackResultVO> submit(@Valid @RequestBody FeedbackRequest request) {
        return ApiResponse.success(feedbackService.submit(request));
    }
}
