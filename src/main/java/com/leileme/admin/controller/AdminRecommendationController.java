package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminRecommendationSaveRequest;
import com.leileme.admin.dto.AdminRecommendationStatusRequest;
import com.leileme.admin.entity.HomeRecommendation;
import com.leileme.admin.service.AdminRecommendationService;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-首页推荐位管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/recommendations")
public class AdminRecommendationController {
    private final AdminRecommendationService adminRecommendationService;

    public AdminRecommendationController(AdminRecommendationService adminRecommendationService) {
        this.adminRecommendationService = adminRecommendationService;
    }

    /**
     * 分页列表。
     */
    @GetMapping
    public ApiResponse<PageResult<HomeRecommendation>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String slotCode,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminRecommendationService.page(page, size, slotCode, status));
    }

    /**
     * 详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<HomeRecommendation> detail(@PathVariable Long id) {
        return ApiResponse.success(adminRecommendationService.detail(id));
    }

    /**
     * 新建。
     */
    @PostMapping
    public ApiResponse<HomeRecommendation> create(@Valid @RequestBody AdminRecommendationSaveRequest request) {
        return ApiResponse.success(adminRecommendationService.create(request));
    }

    /**
     * 更新。
     */
    @PutMapping("/{id}")
    public ApiResponse<HomeRecommendation> update(@PathVariable Long id,
                                                   @Valid @RequestBody AdminRecommendationSaveRequest request) {
        return ApiResponse.success(adminRecommendationService.update(id, request));
    }

    /**
     * 更新启停状态。
     */
    @PutMapping("/{id}/status")
    public ApiResponse<HomeRecommendation> updateStatus(@PathVariable Long id,
                                                          @Valid @RequestBody AdminRecommendationStatusRequest request) {
        return ApiResponse.success(adminRecommendationService.updateStatus(id, request));
    }

    /**
     * 删除。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminRecommendationService.delete(id);
        return ApiResponse.success(null);
    }
}
