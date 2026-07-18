package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.admin.config.AdminAuthProperties;
import com.leileme.admin.config.AdminJwtUtil;
import com.leileme.admin.dto.AdminLoginRequest;
import com.leileme.admin.entity.AdminUser;
import com.leileme.admin.mapper.AdminUserMapper;
import com.leileme.admin.vo.AdminLoginResponse;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.exception.UnauthorizedException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 管理员登录服务。密码使用 BCrypt 加密。
 */
@Service
public class AdminAuthService {
    private final AdminUserMapper adminUserMapper;
    private final AdminJwtUtil adminJwtUtil;
    private final AdminAuthProperties properties;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthService(AdminUserMapper adminUserMapper,
                            AdminJwtUtil adminJwtUtil,
                            AdminAuthProperties properties,
                            PasswordEncoder passwordEncoder) {
        this.adminUserMapper = adminUserMapper;
        this.adminJwtUtil = adminJwtUtil;
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser admin = adminUserMapper.selectOne(Wrappers.lambdaQuery(AdminUser.class)
                .eq(AdminUser::getUsername, request.username()));
        if (admin == null) {
            throw new UnauthorizedException(40102, "用户名或密码错误");
        }
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            throw new UnauthorizedException(40103, "账号已被禁用");
        }
        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new UnauthorizedException(40102, "用户名或密码错误");
        }
        // 更新最后登录时间
        admin.setLastLoginAt(LocalDateTime.now());
        adminUserMapper.updateById(admin);

        String token = adminJwtUtil.issue(admin.getId(), admin.getUsername());
        return new AdminLoginResponse(
                token,
                properties.getJwt().getExpiresIn(),
                new AdminLoginResponse.AdminUserInfo(admin.getId(), admin.getUsername(), admin.getDisplayName())
        );
    }

    /**
     * 获取当前登录管理员信息（用于前端刷新页面后恢复会话）。
     */
    public AdminLoginResponse.AdminUserInfo currentAdmin(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null || admin.getStatus() == null || admin.getStatus() != 1) {
            throw new UnauthorizedException(40101, "管理员不存在或已被禁用");
        }
        return new AdminLoginResponse.AdminUserInfo(admin.getId(), admin.getUsername(), admin.getDisplayName());
    }

    /**
     * 修改当前管理员密码。
     */
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(40401, "管理员不存在");
        }
        if (!passwordEncoder.matches(oldPassword, admin.getPasswordHash())) {
            throw new BusinessException(40001, "原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 100) {
            throw new BusinessException(40001, "新密码长度需在 6 到 100 字之间");
        }
        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        adminUserMapper.updateById(admin);
    }

    /**
     * 启动时检查 admin_user 表，若为空则插入默认管理员账号（admin/admin123）。
     * 生产环境必须通过环境变量覆盖默认密码或登录后立即修改。
     */
    @Configuration
    public static class AdminBootstrap {
        @Bean
        ApplicationRunner adminBootstrapRunner(AdminUserMapper mapper,
                                               AdminAuthProperties properties,
                                               PasswordEncoder passwordEncoder) {
            return args -> {
                if (!properties.getBootstrap().isEnabled()) return;
                Long count = mapper.selectCount(null);
                if (count != null && count > 0) return;
                AdminUser admin = new AdminUser();
                admin.setUsername(properties.getBootstrap().getUsername());
                admin.setPasswordHash(passwordEncoder.encode(properties.getBootstrap().getPassword()));
                admin.setDisplayName(properties.getBootstrap().getDisplayName());
                admin.setStatus(1);
                mapper.insert(admin);
            };
        }
    }
}
