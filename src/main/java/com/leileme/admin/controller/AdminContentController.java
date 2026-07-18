package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminContentSaveRequest;
import com.leileme.admin.dto.AdminContentStatusRequest;
import com.leileme.admin.service.AdminContentService;
import com.leileme.admin.vo.AdminContentVO;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-内容管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/contents")
public class AdminContentController {
    private final AdminContentService adminContentService;

    public AdminContentController(AdminContentService adminContentService) {
        this.adminContentService = adminContentService;
    }

    /**
     * 分页列表。
     */
    @GetMapping
    public ApiResponse<PageResult<AdminContentVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(adminContentService.page(page, size, keyword, status, contentType, categoryId));
    }

    /**
     * 详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<AdminContentVO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.detail(id));
    }

    /**
     * 新建（草稿）。
     */
    @PostMapping
    public ApiResponse<AdminContentVO> create(@Valid @RequestBody AdminContentSaveRequest request) {
        return ApiResponse.success(adminContentService.create(request));
    }

    /**
     * 更新。
     */
    @PutMapping("/{id}")
    public ApiResponse<AdminContentVO> update(@PathVariable Long id,
                                               @Valid @RequestBody AdminContentSaveRequest request) {
        return ApiResponse.success(adminContentService.update(id, request));
    }

    /**
     * 更新状态。status：0=草稿 1=发布 2=下架。
     */
    @PutMapping("/{id}/status")
    public ApiResponse<AdminContentVO> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody AdminContentStatusRequest request) {
        return ApiResponse.success(adminContentService.updateStatus(id, request));
    }

    /**
     * 删除。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminContentService.delete(id);
        return ApiResponse.success(null);
    }
}
