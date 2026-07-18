package com.leileme.common.config;

import com.leileme.admin.config.AdminAuthInterceptor;
import com.leileme.common.auth.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor, AdminAuthInterceptor adminAuthInterceptor) {
        this.authInterceptor = authInterceptor;
        this.adminAuthInterceptor = adminAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 小程序用户接口：/api/v1/** 解析 Token 填充 UserContext（不阻断）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/v1/**");

        // 管理后台接口：/api/admin/v1/** 阻断式鉴权，登录接口排除
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/v1/**")
                .excludePathPatterns("/api/admin/v1/auth/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许 admin-web 开发服务器跨域访问后台接口
        registry.addMapping("/api/admin/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
