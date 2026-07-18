-- ============================================================
-- V003: content 表 category_id 改为允许 NULL
-- 适用场景：管理后台新建内容时允许不选分类
--
-- 背景：
--   v0.1 content 表 category_id 为 BIGINT NOT NULL，小程序场景下所有内容
--   都强制有分类。管理后台 MVP 新建内容时允许不选分类（categoryId=null），
--   原约束会导致 MySQL 抛出 "Field 'category_id' doesn't have a default value"。
--
-- 幂等性：
--   通过 information_schema 检查 IS_NULLABLE，已经是 YES 则跳过。
-- ============================================================

SET @col_nullable := (
    SELECT IS_NULLABLE FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'content'
      AND COLUMN_NAME = 'category_id'
);
SET @sql := IF(@col_nullable = 'NO',
    'ALTER TABLE content MODIFY COLUMN category_id BIGINT DEFAULT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
