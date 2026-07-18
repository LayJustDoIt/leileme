package com.leileme.admin.controller;

import com.leileme.admin.service.AdminDashboardService;
import com.leileme.admin.vo.DashboardSummaryVO;
import com.leileme.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台-基础数据概览接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/dashboard")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    /**
     * 返回基础数据（内容总数/已发布数/草稿数/分类数/标签数/反馈待处理数/反馈总数/今日新增内容数/今日新增反馈数）。
     */
    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryVO> summary() {
        return ApiResponse.success(adminDashboardService.summary());
    }
}
