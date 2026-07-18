package com.leileme.admin.controller;

import com.leileme.admin.dto.AdminPublicConfigUpdateRequest;
import com.leileme.admin.service.AdminPublicConfigService;
import com.leileme.common.config.PublicConfigProperties;
import com.leileme.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-公共配置管理接口。
 * 路径前缀：/api/admin/v1
 */
@RestController
@RequestMapping("/api/admin/v1/public-config")
public class AdminPublicConfigController {
    private final AdminPublicConfigService adminPublicConfigService;

    public AdminPublicConfigController(AdminPublicConfigService adminPublicConfigService) {
        this.adminPublicConfigService = adminPublicConfigService;
    }

    /**
     * 读取当前配置。
     */
    @GetMapping
    public ApiResponse<PublicConfigProperties> get() {
        return ApiResponse.success(adminPublicConfigService.get());
    }

    /**
     * 更新配置。
     */
    @PutMapping
    public ApiResponse<PublicConfigProperties> update(@Valid @RequestBody AdminPublicConfigUpdateRequest request) {
        return ApiResponse.success(adminPublicConfigService.update(request));
    }
}
