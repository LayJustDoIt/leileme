-- V004: search_record.original_keyword / normalized_keyword 改为允许 NULL
-- 原因：分类浏览模式（categoryId 有值且 keyword 为空）下，后端不会传入关键词，
-- 上述两个字段需要允许 NULL，否则会触发 "Field 'normalized_keyword' doesn't have a default value"。
-- 幂等迁移：通过 information_schema 判断当前是否已可空，避免重复执行报错。

SET @db_name = DATABASE();
SET @col_check_orig = (
    SELECT IS_NULLABLE
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'search_record'
      AND COLUMN_NAME = 'original_keyword'
);
SET @col_check_norm = (
    SELECT IS_NULLABLE
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'search_record'
      AND COLUMN_NAME = 'normalized_keyword'
);

SET @sql_orig = IF(@col_check_orig = 'NO',
    'ALTER TABLE search_record MODIFY COLUMN original_keyword VARCHAR(255) DEFAULT NULL',
    'SELECT ''original_keyword already nullable'' AS msg');
PREPARE stmt_orig FROM @sql_orig;
EXECUTE stmt_orig;
DEALLOCATE PREPARE stmt_orig;

SET @sql_norm = IF(@col_check_norm = 'NO',
    'ALTER TABLE search_record MODIFY COLUMN normalized_keyword VARCHAR(255) DEFAULT NULL',
    'SELECT ''normalized_keyword already nullable'' AS msg');
PREPARE stmt_norm FROM @sql_norm;
EXECUTE stmt_norm;
DEALLOCATE PREPARE stmt_norm;
