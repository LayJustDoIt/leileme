package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminFeedbackNoteRequest;
import com.leileme.admin.dto.AdminFeedbackStatusRequest;
import com.leileme.admin.service.AdminFeedbackService;
import com.leileme.admin.vo.AdminFeedbackVO;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-反馈记录管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/feedbacks")
public class AdminFeedbackController {
    private final AdminFeedbackService adminFeedbackService;

    public AdminFeedbackController(AdminFeedbackService adminFeedbackService) {
        this.adminFeedbackService = adminFeedbackService;
    }

    /**
     * 分页列表。
     */
    @GetMapping
    public ApiResponse<PageResult<AdminFeedbackVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(adminFeedbackService.page(page, size, keyword, status));
    }

    /**
     * 详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<AdminFeedbackVO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminFeedbackService.detail(id));
    }

    /**
     * 更新处理状态。status：PENDING/PROCESSED/IGNORED。
     */
    @PutMapping("/{id}/status")
    public ApiResponse<AdminFeedbackVO> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody AdminFeedbackStatusRequest request) {
        return ApiResponse.success(adminFeedbackService.updateStatus(id, request));
    }

    /**
     * 更新管理员备注。
     */
    @PutMapping("/{id}/note")
    public ApiResponse<AdminFeedbackVO> updateNote(@PathVariable Long id,
                                                     @Valid @RequestBody AdminFeedbackNoteRequest request) {
        return ApiResponse.success(adminFeedbackService.updateNote(id, request));
    }
}
