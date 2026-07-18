package com.leileme.common.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 登录与微信相关配置。真实密钥通过环境变量注入，禁止写入仓库。
 *
 * 环境变量：
 *   WECHAT_APP_ID        微信小程序 AppID
 *   WECHAT_APP_SECRET    微信小程序 AppSecret（仅后端持有）
 *   JWT_SECRET           JWT 签名密钥
 *   WECHAT_MOCK_ENABLED  开发环境 mock 登录开关（true/false）
 */
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private Wechat wechat = new Wechat();
    private Jwt jwt = new Jwt();

    public Wechat getWechat() { return wechat; }
    public void setWechat(Wechat wechat) { this.wechat = wechat; }
    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }

    public static class Wechat {
        private String appId = "";
        private String appSecret = "";
        private boolean mockEnabled = false;

        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        public String getAppSecret() { return appSecret; }
        public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
        public boolean isMockEnabled() { return mockEnabled; }
        public void setMockEnabled(boolean mockEnabled) { this.mockEnabled = mockEnabled; }
    }

    public static class Jwt {
        private String secret = "";
        private long expiresIn = 604800L; // 7 天，单位秒

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    }
}
