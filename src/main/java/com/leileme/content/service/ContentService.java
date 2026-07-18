package com.leileme.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.common.exception.BusinessException;
import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.content.vo.ContentDetailVO;
import com.leileme.content.vo.RandomTipVO;
import com.leileme.history.entity.UserBrowseRecord;
import com.leileme.history.mapper.UserBrowseRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContentService {
    private final ContentMapper contentMapper;
    private final UserBrowseRecordMapper browseRecordMapper;
    private final ContentAssembler assembler;

    public ContentService(ContentMapper contentMapper, UserBrowseRecordMapper browseRecordMapper,
                          ContentAssembler assembler) {
        this.contentMapper = contentMapper;
        this.browseRecordMapper = browseRecordMapper;
        this.assembler = assembler;
    }

    public ContentDetailVO getDetail(Long id, Long userId, String sessionId) {
        Content content = contentMapper.selectById(id);
        if (content == null || content.getStatus() == null || content.getStatus() != 1) {
            throw new BusinessException(40401, "内容不存在");
        }

        contentMapper.incrementViewCount(id);
        writeBrowseRecord(content.getId(), userId, sessionId);

        List<ContentCardVO> related = contentMapper.selectRelated(content.getCategoryId(), content.getId(), 4)
                .stream().map(assembler::fromEntity).toList();

        return new ContentDetailVO(
                content.getId(), content.getContentType(), content.getTitle(), content.getSummary(), content.getBody(),
                content.getCoverUrl(), content.getSourceName(), content.getSourceUrl(), content.getAuthorName(),
                contentMapper.selectTagNames(content.getId()), content.getViewCount() + 1,
                content.getFavoriteCount(), content.getShareCount(), content.getPublishedAt(), false, related
        );
    }

    public RandomTipVO randomTip() {
        long count = contentMapper.selectCount(Wrappers.lambdaQuery(Content.class)
                .eq(Content::getStatus, 1)
                .eq(Content::getContentType, "RANDOM_TIP"));
        if (count == 0) {
            throw new BusinessException(40401, "暂时没有随机建议");
        }
        long offset = ThreadLocalRandom.current().nextLong(count);
        Page<Content> page = new Page<>(offset + 1, 1, false);
        List<Content> rows = contentMapper.selectPage(page, Wrappers.lambdaQuery(Content.class)
                        .eq(Content::getStatus, 1)
                        .eq(Content::getContentType, "RANDOM_TIP")
                        .orderByAsc(Content::getId))
                .getRecords();
        Content item = rows.getFirst();
        return new RandomTipVO(item.getId(), item.getTitle(), item.getSummary());
    }

    private void writeBrowseRecord(Long contentId, Long userId, String sessionId) {
        UserBrowseRecord record = new UserBrowseRecord();
        record.setUserId(userId);
        record.setSessionId(sessionId);
        record.setContentId(contentId);
        record.setBrowseCount(1);
        record.setFirstBrowsedAt(LocalDateTime.now());
        record.setLastBrowsedAt(LocalDateTime.now());
        browseRecordMapper.insert(record);
    }
}
