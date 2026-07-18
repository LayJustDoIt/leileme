package com.leileme.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容-标签关联表实体。
 * 保存内容时先删除旧关联再插入新关联。
 */
@Data
@TableName("content_tag_relation")
public class ContentTagRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long contentId;
    private Long tagId;
    private LocalDateTime createdAt;
}
