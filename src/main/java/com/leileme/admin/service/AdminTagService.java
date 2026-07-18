package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.admin.dto.AdminTagSaveRequest;
import com.leileme.admin.entity.ContentTag;
import com.leileme.admin.mapper.ContentTagMapper;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台-标签服务。
 * status：1=启用 0=禁用。
 */
@Service
public class AdminTagService {
    private final ContentTagMapper tagMapper;

    public AdminTagService(ContentTagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /**
     * 分页查询标签列表。
     */
    public PageResult<ContentTag> page(int page, int size, String keyword) {
        Page<ContentTag> p = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : Math.min(size, 50));
        IPage<ContentTag> result = tagMapper.selectPage(p, Wrappers.lambdaQuery(ContentTag.class)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(ContentTag::getTagName, keyword)
                        .or().like(ContentTag::getTagCode, keyword))
                .orderByDesc(ContentTag::getId));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(),
                result.getCurrent() < result.getPages(), result.getRecords());
    }

    /**
     * 全量列表。
     */
    public List<ContentTag> all() {
        return tagMapper.selectList(Wrappers.lambdaQuery(ContentTag.class)
                .orderByDesc(ContentTag::getId));
    }

    /**
     * 新建标签。
     */
    @Transactional
    public ContentTag create(AdminTagSaveRequest request) {
        // 校验 tagCode 唯一
        Long exists = tagMapper.selectCount(Wrappers.lambdaQuery(ContentTag.class)
                .eq(ContentTag::getTagCode, request.tagCode()));
        if (exists != null && exists > 0) {
            throw new BusinessException(40001, "标签编码已存在");
        }
        ContentTag tag = new ContentTag();
        applyToEntity(tag, request);
        tag.setUseCount(0L);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag;
    }

    /**
     * 更新标签。
     */
    @Transactional
    public ContentTag update(Long id, AdminTagSaveRequest request) {
        ContentTag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(40401, "标签不存在");
        }
        // 校验 tagCode 唯一（排除自身）
        Long exists = tagMapper.selectCount(Wrappers.lambdaQuery(ContentTag.class)
                .eq(ContentTag::getTagCode, request.tagCode())
                .ne(ContentTag::getId, id));
        if (exists != null && exists > 0) {
            throw new BusinessException(40001, "标签编码已存在");
        }
        applyToEntity(tag, request);
        tag.setUpdatedAt(LocalDateTime.now());
        tagMapper.updateById(tag);
        return tag;
    }

    /**
     * 删除标签。
     */
    @Transactional
    public void delete(Long id) {
        ContentTag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(40401, "标签不存在");
        }
        tagMapper.deleteById(id);
    }

    /**
     * 将请求字段应用到实体上。
     */
    private void applyToEntity(ContentTag tag, AdminTagSaveRequest request) {
        tag.setTagName(request.tagName());
        tag.setTagCode(request.tagCode());
        tag.setStatus(request.status());
    }
}
