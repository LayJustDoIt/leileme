package com.leileme.home.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.service.ContentAssembler;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.home.entity.HotKeyword;
import com.leileme.home.mapper.HotKeywordMapper;
import com.leileme.home.vo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeService {
    private final HotKeywordMapper hotKeywordMapper;
    private final ContentMapper contentMapper;
    private final ContentAssembler assembler;

    public HomeService(HotKeywordMapper hotKeywordMapper, ContentMapper contentMapper, ContentAssembler assembler) {
        this.hotKeywordMapper = hotKeywordMapper;
        this.contentMapper = contentMapper;
        this.assembler = assembler;
    }

    public HomeVO getHome() {
        LocalDateTime now = LocalDateTime.now();
        List<HotKeywordVO> hotKeywords = hotKeywordMapper.selectList(
                        Wrappers.lambdaQuery(HotKeyword.class)
                                .eq(HotKeyword::getStatus, 1)
                                .and(w -> w.isNull(HotKeyword::getStartAt).or().le(HotKeyword::getStartAt, now))
                                .and(w -> w.isNull(HotKeyword::getEndAt).or().ge(HotKeyword::getEndAt, now))
                                .orderByDesc(HotKeyword::getSortNo)
                                .last("LIMIT 10"))
                .stream().map(k -> new HotKeywordVO(k.getKeyword(), k.getDisplayName(), k.getIcon())).toList();

        List<Content> recommended = contentMapper.selectList(Wrappers.lambdaQuery(Content.class)
                .eq(Content::getStatus, 1)
                .ne(Content::getContentType, "RANDOM_TIP")
                .le(Content::getPublishedAt, now)
                .orderByDesc(Content::getIsTop)
                .orderByDesc(Content::getHotScore)
                .last("LIMIT 3"));

        List<Content> guess = contentMapper.selectList(Wrappers.lambdaQuery(Content.class)
                .eq(Content::getStatus, 1)
                .ne(Content::getContentType, "RANDOM_TIP")
                .le(Content::getPublishedAt, now)
                .orderByDesc(Content::getViewCount)
                .last("LIMIT 6"));

        return new HomeVO(
                greeting(),
                hotKeywords,
                List.of(
                        new SceneEntryVO("MAKE_MONEY", "想赚钱", "money", "副业"),
                        new SceneEntryVO("LEARN", "想学习", "book", "学习"),
                        new SceneEntryVO("RELAX", "想放松", "sofa", "摸鱼"),
                        new SceneEntryVO("AI", "玩 AI", "robot", "AI工具"),
                        new SceneEntryVO("IDEA", "找灵感", "bulb", "创作灵感")
                ),
                recommended.stream().map(assembler::fromEntity).toList(),
                List.of(
                        new AudienceVO(1L, "OFFICE_WORKER", "上班族", "摸鱼、职场、效率与下班生活"),
                        new AudienceVO(2L, "CREATOR", "自媒体", "选题、脚本、标题与爆款案例"),
                        new AudienceVO(3L, "STUDENT", "学生党", "学习资源、校园生活与成长灵感"),
                        new AudienceVO(4L, "PARENT", "宝妈专区", "亲子学习、育儿工具与家庭安排")
                ),
                guess.stream().map(assembler::fromEntity).toList()
        );
    }

    private String greeting() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 5) return "夜深了，别太累，随便看看吧";
        if (hour < 11) return "早上好，今天想先做什么";
        if (hour < 14) return "中午好，休息一下再出发";
        if (hour < 18) return "下午好，累了就来逛一会儿";
        return "晚上好，看看今天有什么新鲜事";
    }
}
