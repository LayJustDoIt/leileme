package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminCategorySaveRequest;
import com.leileme.admin.service.AdminCategoryService;
import com.leileme.category.entity.ContentCategory;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台-分类管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/categories")
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    /**
     * 分页列表。
     */
    @GetMapping
    public ApiResponse<PageResult<ContentCategory>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminCategoryService.page(page, size, keyword));
    }

    /**
     * 全量列表（用于下拉选择，不分页）。
     */
    @GetMapping("/all")
    public ApiResponse<List<ContentCategory>> all() {
        return ApiResponse.success(adminCategoryService.all());
    }

    /**
     * 详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<ContentCategory> detail(@PathVariable Long id) {
        return ApiResponse.success(adminCategoryService.detail(id));
    }

    /**
     * 新建。
     */
    @PostMapping
    public ApiResponse<ContentCategory> create(@Valid @RequestBody AdminCategorySaveRequest request) {
        return ApiResponse.success(adminCategoryService.create(request));
    }

    /**
     * 更新。
     */
    @PutMapping("/{id}")
    public ApiResponse<ContentCategory> update(@PathVariable Long id,
                                                @Valid @RequestBody AdminCategorySaveRequest request) {
        return ApiResponse.success(adminCategoryService.update(id, request));
    }

    /**
     * 删除。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return ApiResponse.success(null);
    }
}
