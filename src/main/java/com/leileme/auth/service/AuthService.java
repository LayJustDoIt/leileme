package com.leileme.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.auth.dto.WechatLoginRequest;
import com.leileme.auth.vo.LoginResponse;
import com.leileme.auth.vo.UserVO;
import com.leileme.common.auth.AuthProperties;
import com.leileme.common.auth.JwtUtil;
import com.leileme.common.exception.BusinessException;
import com.leileme.history.service.HistoryService;
import com.leileme.user.entity.UserAccount;
import com.leileme.user.mapper.UserAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthProperties authProperties;
    private final JwtUtil jwtUtil;
    private final UserAccountMapper userAccountMapper;
    private final HistoryService historyService;

    public AuthService(AuthProperties authProperties,
                       JwtUtil jwtUtil,
                       UserAccountMapper userAccountMapper,
                       HistoryService historyService) {
        this.authProperties = authProperties;
        this.jwtUtil = jwtUtil;
        this.userAccountMapper = userAccountMapper;
        this.historyService = historyService;
    }

    @Transactional
    public LoginResponse login(WechatLoginRequest request) {
        String code = request.code();
        String sessionId = request.sessionId();

        String openid;
        if (authProperties.getWechat().isMockEnabled()) {
            // 开发环境 mock：相同 code 对应同一模拟 openid
            openid = "mock-" + code;
        } else {
            // 正式环境：调用微信 jscode2session 换取 openid
            openid = exchangeCodeForOpenid(code);
        }

        if (openid == null || openid.isBlank()) {
            throw new BusinessException(40101, "登录失败：无法获取用户身份");
        }

        // 按 openid 查询用户，不存在则创建
        UserAccount user = userAccountMapper.selectOne(Wrappers.lambdaQuery(UserAccount.class)
                .eq(UserAccount::getOpenid, openid)
                .last("LIMIT 1"));
        if (user == null) {
            user = new UserAccount();
            user.setOpenid(openid);
            user.setStatus(1);
            user.setLastLoginAt(LocalDateTime.now());
            userAccountMapper.insert(user);
        } else {
            user.setLastLoginAt(LocalDateTime.now());
            userAccountMapper.updateById(user);
        }

        // 合并匿名浏览数据到当前用户
        if (sessionId != null && !sessionId.isBlank()) {
            try {
                historyService.mergeAnonymousData(user.getId(), sessionId);
            } catch (Exception e) {
                log.warn("合并匿名数据失败，userId={}, sessionId={}", user.getId(), sessionId, e);
            }
        }

        // 签发 Token
        String token = jwtUtil.issue(user.getId());

        return new LoginResponse(
                token,
                authProperties.getJwt().getExpiresIn(),
                new UserVO(user.getId(), user.getNickname(), user.getAvatarUrl())
        );
    }

    /**
     * 调用微信 jscode2session 接口换取 openid。
     * AppSecret 仅在后端使用，不返回给前端。
     */
    private String exchangeCodeForOpenid(String code) {
        String appId = authProperties.getWechat().getAppId();
        String appSecret = authProperties.getWechat().getAppSecret();
        if (appId == null || appId.isBlank() || appSecret == null || appSecret.isBlank()) {
            throw new BusinessException(50001, "微信登录未配置：缺少 AppID 或 AppSecret");
        }

        RestClient restClient = RestClient.create();
        Map<String, Object> resp;
        try {
            resp = restClient.get()
                    .uri("https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code",
                            appId, appSecret, code)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("调用微信 jscode2session 失败", e);
            throw new BusinessException(40102, "登录失败：微信接口调用异常");
        }

        if (resp == null) {
            throw new BusinessException(40103, "登录失败：微信返回为空");
        }
        Object errcode = resp.get("errcode");
        if (errcode != null && !errcode.equals(0)) {
            log.warn("微信 jscode2session 返回错误: {}", resp);
            throw new BusinessException(40104, "登录失败：" + resp.getOrDefault("errmsg", "微信返回错误"));
        }
        Object openidObj = resp.get("openid");
        if (openidObj == null) {
            throw new BusinessException(40105, "登录失败：微信未返回 openid");
        }
        return openidObj.toString();
    }
}
