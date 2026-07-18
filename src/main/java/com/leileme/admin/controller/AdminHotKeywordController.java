package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminHotKeywordSaveRequest;
import com.leileme.admin.service.AdminHotKeywordService;
import com.leileme.home.entity.HotKeyword;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台-热门搜索词管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/hot-keywords")
public class AdminHotKeywordController {
    private final AdminHotKeywordService adminHotKeywordService;

    public AdminHotKeywordController(AdminHotKeywordService adminHotKeywordService) {
        this.adminHotKeywordService = adminHotKeywordService;
    }

    /**
     * 分页列表。
     */
    @GetMapping
    public ApiResponse<PageResult<HotKeyword>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminHotKeywordService.page(page, size, keyword));
    }

    /**
     * 全量列表。
     */
    @GetMapping("/all")
    public ApiResponse<List<HotKeyword>> all() {
        return ApiResponse.success(adminHotKeywordService.all());
    }

    /**
     * 新建。
     */
    @PostMapping
    public ApiResponse<HotKeyword> create(@Valid @RequestBody AdminHotKeywordSaveRequest request) {
        return ApiResponse.success(adminHotKeywordService.create(request));
    }

    /**
     * 更新。
     */
    @PutMapping("/{id}")
    public ApiResponse<HotKeyword> update(@PathVariable Long id,
                                            @Valid @RequestBody AdminHotKeywordSaveRequest request) {
        return ApiResponse.success(adminHotKeywordService.update(id, request));
    }

    /**
     * 删除。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminHotKeywordService.delete(id);
        return ApiResponse.success(null);
    }
}
