package com.leileme.home.vo;

import com.leileme.content.vo.ContentCardVO;

import java.util.List;

public record HomeVO(
        String greeting,
        List<HotKeywordVO> hotKeywords,
        List<SceneEntryVO> sceneEntries,
        List<ContentCardVO> todayRecommendations,
        List<AudienceVO> audiences,
        List<ContentCardVO> guessYouLike
) {}
