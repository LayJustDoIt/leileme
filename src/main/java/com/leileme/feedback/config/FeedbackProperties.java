package com.leileme.feedback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 反馈功能相关配置。可通过环境变量覆盖。
 */
@Component
@ConfigurationProperties(prefix = "feedback")
public class FeedbackProperties {
    /** 同一用户/会话最小重复提交间隔（秒） */
    private long minIntervalSeconds = 60L;
    /** 单日每用户/会话最大提交数 */
    private int dailyLimit = 10;

    public long getMinIntervalSeconds() { return minIntervalSeconds; }
    public void setMinIntervalSeconds(long minIntervalSeconds) { this.minIntervalSeconds = minIntervalSeconds; }
    public int getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(int dailyLimit) { this.dailyLimit = dailyLimit; }
}
