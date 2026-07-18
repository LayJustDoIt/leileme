package com.leileme.content.service;

import com.leileme.content.entity.Content;
import com.leileme.content.mapper.ContentMapper;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.search.mapper.model.SearchContentRow;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ContentAssembler {
    private final ContentMapper contentMapper;

    public ContentAssembler(ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    public ContentCardVO fromEntity(Content content) {
        return fromEntity(content, false);
    }

    public ContentCardVO fromEntity(Content content, boolean favorite) {
        return new ContentCardVO(
                content.getId(), content.getContentType(), content.getTitle(), content.getSummary(),
                content.getCoverUrl(), content.getSourceName(), contentMapper.selectTagNames(content.getId()),
                content.getViewCount(), content.getFavoriteCount(), content.getPublishedAt(), favorite
        );
    }

    public ContentCardVO fromEntity(Content content, Set<Long> favoritedContentIds) {
        return fromEntity(content, favoritedContentIds != null && favoritedContentIds.contains(content.getId()));
    }

    public ContentCardVO fromSearchRow(SearchContentRow row) {
        return new ContentCardVO(
                row.getId(), row.getContentType(), row.getTitle(), row.getSummary(),
                row.getCoverUrl(), row.getSourceName(), contentMapper.selectTagNames(row.getId()),
                row.getViewCount(), row.getFavoriteCount(), row.getPublishedAt(), false
        );
    }
}
