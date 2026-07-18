package com.leileme.category.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leileme.category.mapper.ContentCategoryMapper;
import com.leileme.category.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final ContentCategoryMapper categoryMapper;

    public CategoryService(ContentCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryVO> listEnabled() {
        return categoryMapper.selectList(Wrappers.lambdaQuery(com.leileme.category.entity.ContentCategory.class)
                        .eq(com.leileme.category.entity.ContentCategory::getStatus, 1)
                        .orderByDesc(com.leileme.category.entity.ContentCategory::getSortNo))
                .stream()
                .map(c -> new CategoryVO(c.getId(), c.getCategoryCode(), c.getCategoryName(),
                        c.getIcon(), c.getDescription(), c.getSortNo()))
                .toList();
    }
}
