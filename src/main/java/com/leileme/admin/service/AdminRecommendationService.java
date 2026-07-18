package com.leileme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leileme.admin.dto.AdminRecommendationSaveRequest;
import com.leileme.admin.dto.AdminRecommendationStatusRequest;
import com.leileme.admin.entity.HomeRecommendation;
import com.leileme.admin.mapper.HomeRecommendationMapper;
import com.leileme.common.exception.BusinessException;
import com.leileme.common.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理后台-首页推荐位服务。
 * status：1=启用 0=禁用。
 */
@Service
public class AdminRecommendationService {
    private final HomeRecommendationMapper recommendationMapper;

    public AdminRecommendationService(HomeRecommendationMapper recommendationMapper) {
        this.recommendationMapper = recommendationMapper;
    }

    /**
     * 分页查询推荐位列表。
     */
    public PageResult<HomeRecommendation> page(int page, int size, String slotCode, Integer status) {
        Page<HomeRecommendation> p = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : Math.min(size, 50));
        IPage<HomeRecommendation> result = recommendationMapper.selectPage(p, Wrappers.lambdaQuery(HomeRecommendation.class)
                .eq(slotCode != null && !slotCode.isBlank(), HomeRecommendation::getSlotCode, slotCode)
                .eq(status != null, HomeRecommendation::getStatus, status)
                .orderByAsc(HomeRecommendation::getSortNo)
                .orderByDesc(HomeRecommendation::getId));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(),
                result.getCurrent() < result.getPages(), result.getRecords());
    }

    /**
     * 推荐位详情。
     */
    public HomeRecommendation detail(Long id) {
        HomeRecommendation rec = recommendationMapper.selectById(id);
        if (rec == null) {
            throw new BusinessException(40401, "推荐位不存在");
        }
        return rec;
    }

    /**
     * 新建推荐位。
     */
    @Transactional
    public HomeRecommendation create(AdminRecommendationSaveRequest request) {
        HomeRecommendation rec = new HomeRecommendation();
        applyToEntity(rec, request);
        rec.setCreatedAt(LocalDateTime.now());
        rec.setUpdatedAt(LocalDateTime.now());
        recommendationMapper.insert(rec);
        return rec;
    }

    /**
     * 更新推荐位。
     */
    @Transactional
    public HomeRecommendation update(Long id, AdminRecommendationSaveRequest request) {
        HomeRecommendation rec = recommendationMapper.selectById(id);
        if (rec == null) {
            throw new BusinessException(40401, "推荐位不存在");
        }
        applyToEntity(rec, request);
        rec.setUpdatedAt(LocalDateTime.now());
        recommendationMapper.updateById(rec);
        return rec;
    }

    /**
     * 更新推荐位启停状态。
     */
    @Transactional
    public HomeRecommendation updateStatus(Long id, AdminRecommendationStatusRequest request) {
        HomeRecommendation rec = recommendationMapper.selectById(id);
        if (rec == null) {
            throw new BusinessException(40401, "推荐位不存在");
        }
        if (request.status() == null || (request.status() != 0 && request.status() != 1)) {
            throw new BusinessException(40001, "状态值非法");
        }
        rec.setStatus(request.status());
        rec.setUpdatedAt(LocalDateTime.now());
        recommendationMapper.updateById(rec);
        return rec;
    }

    /**
     * 删除推荐位。
     */
    @Transactional
    public void delete(Long id) {
        HomeRecommendation rec = recommendationMapper.selectById(id);
        if (rec == null) {
            throw new BusinessException(40401, "推荐位不存在");
        }
        recommendationMapper.deleteById(id);
    }

    /**
     * 将请求字段应用到实体上。
     */
    private void applyToEntity(HomeRecommendation rec, AdminRecommendationSaveRequest request) {
        rec.setSlotCode(request.slotCode());
        rec.setContentId(request.contentId());
        rec.setCategoryId(request.categoryId());
        rec.setTitleOverride(request.titleOverride());
        rec.setImageOverride(request.imageOverride());
        rec.setSortNo(request.sortNo());
        rec.setStartAt(request.startAt());
        rec.setEndAt(request.endAt());
        rec.setStatus(request.status());
    }
}
