-- ============================================================
-- V002: feedback_record 表新增管理后台扩展字段
-- 适用场景：从 v0.1（小程序版）升级到 v0.2（管理后台 MVP）
--
-- 背景：
--   v0.1 的 feedback_record 表只有提交侧字段（user_id/session_id/content/
--   contact/page_path/submitted_at/created_at），没有管理后台所需的处理状态、
--   管理员备注、处理人、处理时间。
--   schema.sql 中的 CREATE TABLE IF NOT EXISTS 不会给已存在的表补字段，
--   因此老库需要通过本迁移补齐。
--
-- 幂等性：
--   使用 information_schema + PREPARE/EXECUTE 逐字段检查，
--   重复执行不会报错，部分存在的库也能正确补齐缺失字段。
--
-- 字段顺序说明：
--   新建字段追加到表末尾（不强制 AFTER），已存在的字段保持原位。
--   字段顺序不影响查询与业务逻辑。
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
