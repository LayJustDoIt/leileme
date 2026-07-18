package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.admin.dto.AdminCategorySaveRequest;
import com.leileme.category.entity.ContentCategory;
import com.leileme.category.mapper.ContentCategoryMapper;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台-分类服务。
 * status：1=启用 0=禁用。
 */
@Service
public class AdminCategoryService {
    private final ContentCategoryMapper categoryMapper;

    public AdminCategoryService(ContentCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 分页查询分类列表。
     */
    public PageResult<ContentCategory> page(int page, int size, String keyword) {
        Page<ContentCategory> p = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : Math.min(size, 50));
        IPage<ContentCategory> result = categoryMapper.selectPage(p, Wrappers.lambdaQuery(ContentCategory.class)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(ContentCategory::getCategoryName, keyword)
                        .or().like(ContentCategory::getCategoryCode, keyword)
                        .or().like(ContentCategory::getDescription, keyword))
                .orderByAsc(ContentCategory::getSortNo)
                .orderByDesc(ContentCategory::getId));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(),
                result.getCurrent() < result.getPages(), result.getRecords());
    }

    /**
     * 全量列表（用于下拉选择，不分页）。
     */
    public List<ContentCategory> all() {
        return categoryMapper.selectList(Wrappers.lambdaQuery(ContentCategory.class)
                .orderByAsc(ContentCategory::getSortNo)
                .orderByDesc(ContentCategory::getId));
    }

    /**
     * 分类详情。
     */
    public ContentCategory detail(Long id) {
        ContentCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(40401, "分类不存在");
        }
        return category;
    }

    /**
     * 新建分类。
     */
    @Transactional
    public ContentCategory create(AdminCategorySaveRequest request) {
        ContentCategory category = new ContentCategory();
        applyToEntity(category, request);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
        return category;
    }

    /**
     * 更新分类。
     */
    @Transactional
    public ContentCategory update(Long id, AdminCategorySaveRequest request) {
        ContentCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(40401, "分类不存在");
        }
        applyToEntity(category, request);
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        return category;
    }

    /**
     * 删除分类。
     */
    @Transactional
    public void delete(Long id) {
        ContentCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(40401, "分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    /**
     * 将请求字段应用到实体上。
     * 注意：DTO 使用 parentCode（String），实体使用 parentId（Long）。
     * 若 parentCode 为数字字符串则解析为 Long 赋给 parentId。
     */
    private void applyToEntity(ContentCategory category, AdminCategorySaveRequest request) {
        category.setCategoryCode(request.categoryCode());
        category.setCategoryName(request.categoryName());
        category.setIcon(request.icon());
        category.setDescription(request.description());
        category.setSortNo(request.sortNo());
        category.setStatus(request.status());
        if (request.parentCode() != null && !request.parentCode().isBlank()) {
            try {
                category.setParentId(Long.parseLong(request.parentCode()));
            } catch (NumberFormatException e) {
                throw new BusinessException(40001, "parentCode 必须为数字");
            }
        } else {
            category.setParentId(null);
        }
    }
}
