package com.leileme.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.content.entity.Content;
import com.leileme.search.mapper.model.SearchContentRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentMapper extends BaseMapper<Content> {
    IPage<SearchContentRow> selectSearchPage(Page<SearchContentRow> page,
                                              @Param("keyword") String keyword,
                                              @Param("contentType") String contentType,
                                              @Param("categoryId") Long categoryId);

    List<String> selectTagNames(@Param("contentId") Long contentId);

    List<Content> selectRelated(@Param("categoryId") Long categoryId,
                                @Param("excludeId") Long excludeId,
                                @Param("limit") int limit);

    int incrementViewCount(@Param("id") Long id);

    int incrementFavoriteCount(@Param("id") Long id);

    int decrementFavoriteCount(@Param("id") Long id);
}
