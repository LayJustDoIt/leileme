-- ============================================================
-- 累了么 管理后台 MVP 数据库迁移便捷脚本（可重复执行）
--
-- 用途：
--   一键执行管理后台 MVP 所需的全部数据库变更。
--
-- 执行方式：
--   方式 1（Docker 环境，推荐）：
--     cat scripts/migrate-admin-mvp.sql | docker-compose exec -T mysql mysql -uleileme -pleileme123 leileme
--
--   方式 2（本地 MySQL）：
--     mysql -uleileme -pleileme123 leileme < scripts/migrate-admin-mvp.sql
--
--   方式 3（指定主机端口）：
--     mysql -h127.0.0.1 -P3306 -uleileme -pleileme123 leileme < scripts/migrate-admin-mvp.sql
--
-- 执行顺序：
--   1. feedback_record 扩展管理后台字段（status/admin_note/handled_by/handled_at + 索引）
--   （后续新增迁移逻辑在此追加）
--
-- 安全说明：
--   - 全部迁移幂等，重复执行不会报错
--   - 不删除任何已有数据
--   - 不修改小程序接口字段
--   - 禁止使用 docker-compose down -v 清空数据库
--
-- 注意：
--   本脚本内联了 sql/migrations/V002__admin_feedback_fields.sql 的逻辑，
--   不依赖 MySQL 的 SOURCE 命令（SOURCE 路径在不同环境下解析不一致）。
--   单独执行 V002 迁移文件可用：
--     cat sql/migrations/V002__admin_feedback_fields.sql | docker-compose exec -T mysql mysql -uleileme -pleileme123 leileme
-- ============================================================

USE leileme;

-- ============================================================
-- V002: feedback_record 表新增管理后台扩展字段
-- ============================================================

-- 1. status：处理状态 PENDING/PROCESSED/IGNORED
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'feedback_record'
      AND COLUMN_NAME = 'status'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE feedback_record ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT ''PENDING''',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. admin_note：管理员备注
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'feedback_record'
      AND COLUMN_NAME = 'admin_note'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE feedback_record ADD COLUMN admin_note VARCHAR(1000) DEFAULT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. handled_by：处理人管理员 ID
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'feedback_record'
      AND COLUMN_NAME = 'handled_by'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE feedback_record ADD COLUMN handled_by BIGINT DEFAULT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. handled_at：处理时间
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'feedback_record'
      AND COLUMN_NAME = 'handled_at'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE feedback_record ADD COLUMN handled_at DATETIME DEFAULT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. idx_feedback_status：处理状态索引
SET @idx_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'feedback_record'
      AND INDEX_NAME = 'idx_feedback_status'
);
SET @sql := IF(@idx_exists = 0,
    'CREATE INDEX idx_feedback_status ON feedback_record(status, submitted_at)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- V003: content 表 category_id 改为允许 NULL（管理后台新建内容可不选分类）
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

-- ============================================================
-- V004: search_record.original_keyword / normalized_keyword 改为允许 NULL
-- 分类浏览模式（categoryId 有值且 keyword 为空）下不传入关键词，
-- 上述两个字段需要允许 NULL，否则会触发 "Field 'normalized_keyword'
-- doesn't have a default value"。
-- ============================================================

SET @col_nullable := (
    SELECT IS_NULLABLE FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'search_record'
      AND COLUMN_NAME = 'original_keyword'
);
SET @sql := IF(@col_nullable = 'NO',
    'ALTER TABLE search_record MODIFY COLUMN original_keyword VARCHAR(255) DEFAULT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_nullable := (
    SELECT IS_NULLABLE FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'search_record'
      AND COLUMN_NAME = 'normalized_keyword'
);
SET @sql := IF(@col_nullable = 'NO',
    'ALTER TABLE search_record MODIFY COLUMN normalized_keyword VARCHAR(255) DEFAULT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 完成提示
SELECT 'migrate-admin-mvp: done' AS result;
