package com.leileme.admin.controller;

import com.leileme.admin.config.AdminUserContext;
import com.leileme.admin.dto.AdminLoginRequest;
import com.leileme.admin.service.AdminAuthService;
import com.leileme.admin.vo.AdminLoginResponse;
import com.leileme.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员登录与会话接口。登录接口在 WebMvcConfig 中被排除拦截。
 */
@RestController
@RequestMapping("/api/admin/v1/auth")
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(adminAuthService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<AdminLoginResponse.AdminUserInfo> me() {
        return ApiResponse.success(adminAuthService.currentAdmin(AdminUserContext.getAdminId()));
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        adminAuthService.changePassword(AdminUserContext.getAdminId(), oldPassword, newPassword);
        return ApiResponse.success(null);
    }
}
