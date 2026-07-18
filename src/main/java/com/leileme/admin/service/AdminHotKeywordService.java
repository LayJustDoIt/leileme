package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.admin.dto.AdminHotKeywordSaveRequest;
import com.leileme.common.exception.BusinessException;
import com.leileme.home.entity.HotKeyword;
import com.leileme.home.mapper.HotKeywordMapper;
import com.leileme.common.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台-热门搜索词服务。
 * status：1=启用 0=禁用。
 */
@Service
public class AdminHotKeywordService {
    private final HotKeywordMapper hotKeywordMapper;

    public AdminHotKeywordService(HotKeywordMapper hotKeywordMapper) {
        this.hotKeywordMapper = hotKeywordMapper;
    }

    /**
     * 分页查询热词列表。
     */
    public PageResult<HotKeyword> page(int page, int size, String keyword) {
        Page<HotKeyword> p = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : Math.min(size, 50));
        IPage<HotKeyword> result = hotKeywordMapper.selectPage(p, Wrappers.lambdaQuery(HotKeyword.class)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(HotKeyword::getKeyword, keyword)
                        .or().like(HotKeyword::getDisplayName, keyword))
                .orderByDesc(HotKeyword::getSortNo)
                .orderByDesc(HotKeyword::getId));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(),
                result.getCurrent() < result.getPages(), result.getRecords());
    }

    /**
     * 全量列表。
     */
    public List<HotKeyword> all() {
        return hotKeywordMapper.selectList(Wrappers.lambdaQuery(HotKeyword.class)
                .orderByDesc(HotKeyword::getSortNo)
                .orderByDesc(HotKeyword::getId));
    }

    /**
     * 新建热词。
     */
    @Transactional
    public HotKeyword create(AdminHotKeywordSaveRequest request) {
        HotKeyword hotKeyword = new HotKeyword();
        applyToEntity(hotKeyword, request);
        hotKeyword.setClickCount(0L);
        hotKeyword.setCreatedAt(LocalDateTime.now());
        hotKeyword.setUpdatedAt(LocalDateTime.now());
        hotKeywordMapper.insert(hotKeyword);
        return hotKeyword;
    }

    /**
     * 更新热词。
     */
    @Transactional
    public HotKeyword update(Long id, AdminHotKeywordSaveRequest request) {
        HotKeyword hotKeyword = hotKeywordMapper.selectById(id);
        if (hotKeyword == null) {
            throw new BusinessException(40401, "热词不存在");
        }
        applyToEntity(hotKeyword, request);
        hotKeyword.setUpdatedAt(LocalDateTime.now());
        hotKeywordMapper.updateById(hotKeyword);
        return hotKeyword;
    }

    /**
     * 删除热词。
     */
    @Transactional
    public void delete(Long id) {
        HotKeyword hotKeyword = hotKeywordMapper.selectById(id);
        if (hotKeyword == null) {
            throw new BusinessException(40401, "热词不存在");
        }
        hotKeywordMapper.deleteById(id);
    }

    /**
     * 将请求字段应用到实体上。
     */
    private void applyToEntity(HotKeyword hotKeyword, AdminHotKeywordSaveRequest request) {
        hotKeyword.setKeyword(request.keyword());
        hotKeyword.setDisplayName(request.displayName());
        hotKeyword.setIcon(request.icon());
        hotKeyword.setSortNo(request.sortNo());
        hotKeyword.setStartAt(request.startAt());
        hotKeyword.setEndAt(request.endAt());
        hotKeyword.setStatus(request.status());
    }
}
