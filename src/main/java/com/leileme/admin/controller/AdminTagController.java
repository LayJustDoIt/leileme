package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminTagSaveRequest;
import com.leileme.admin.entity.ContentTag;
import com.leileme.admin.service.AdminTagService;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台-标签管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/tags")
public class AdminTagController {
    private final AdminTagService adminTagService;

    public AdminTagController(AdminTagService adminTagService) {
        this.adminTagService = adminTagService;
    }

    /**
     * 分页列表。
     */
    @GetMapping
    public ApiResponse<PageResult<ContentTag>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminTagService.page(page, size, keyword));
    }

    /**
     * 全量列表。
     */
    @GetMapping("/all")
    public ApiResponse<List<ContentTag>> all() {
        return ApiResponse.success(adminTagService.all());
    }

    /**
     * 新建。
     */
    @PostMapping
    public ApiResponse<ContentTag> create(@Valid @RequestBody AdminTagSaveRequest request) {
        return ApiResponse.success(adminTagService.create(request));
    }

    /**
     * 更新。
     */
    @PutMapping("/{id}")
    public ApiResponse<ContentTag> update(@PathVariable Long id,
                                            @Valid @RequestBody AdminTagSaveRequest request) {
        return ApiResponse.success(adminTagService.update(id, request));
    }

    /**
     * 删除。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminTagService.delete(id);
        return ApiResponse.success(null);
    }
}
