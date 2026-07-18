package com.leileme.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leileme.admin.entity.ContentTagRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 内容-标签关联 Mapper。
 */
@Mapper
public interface ContentTagRelationMapper extends BaseMapper<ContentTagRelation> {
}
