package com.leileme.auth.controller;

import com.leileme.auth.dto.WechatLoginRequest;
import com.leileme.auth.service.AuthService;
import com.leileme.auth.vo.LoginResponse;
import com.leileme.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wechat-login")
    public ApiResponse<LoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
