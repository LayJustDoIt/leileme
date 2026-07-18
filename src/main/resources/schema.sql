CREATE TABLE IF NOT EXISTS user_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64) NOT NULL,
    unionid VARCHAR(64) DEFAULT NULL,
    nickname VARCHAR(100) DEFAULT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_openid (openid),
    KEY idx_user_unionid (unionid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS content_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    category_code VARCHAR(50) NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    icon VARCHAR(255) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_code (category_code),
    KEY idx_category_status_sort (status, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_type VARCHAR(30) NOT NULL,
    category_id BIGINT DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255) DEFAULT NULL,
    summary VARCHAR(1000) DEFAULT NULL,
    body LONGTEXT DEFAULT NULL,
    search_keywords VARCHAR(1000) DEFAULT NULL,
    cover_url VARCHAR(500) DEFAULT NULL,
    source_name VARCHAR(100) DEFAULT NULL,
    source_url VARCHAR(1000) DEFAULT NULL,
    author_name VARCHAR(100) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    is_original TINYINT NOT NULL DEFAULT 0,
    is_top TINYINT NOT NULL DEFAULT 0,
    hot_score DECIMAL(12,2) NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    favorite_count BIGINT NOT NULL DEFAULT 0,
    share_count BIGINT NOT NULL DEFAULT 0,
    published_at DATETIME DEFAULT NULL,
    created_by BIGINT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_content_category (category_id),
    KEY idx_content_type (content_type),
    KEY idx_content_status_publish (status, published_at),
    KEY idx_content_hot (status, hot_score),
    KEY idx_content_title (title(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS content_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(50) NOT NULL,
    tag_code VARCHAR(50) NOT NULL,
    use_count BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tag_code (tag_code),
    UNIQUE KEY uk_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS content_tag_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_content_tag (content_id, tag_id),
    KEY idx_relation_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS audience (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    audience_code VARCHAR(50) NOT NULL,
    audience_name VARCHAR(50) NOT NULL,
    icon VARCHAR(255) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_audience_code (audience_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS content_audience_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    audience_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_content_audience (content_id, audience_id),
    KEY idx_audience_content (audience_id, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hot_keyword (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    keyword VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) DEFAULT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    click_count BIGINT NOT NULL DEFAULT 0,
    start_at DATETIME DEFAULT NULL,
    end_at DATETIME DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_hot_keyword_status_sort (status, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS home_recommendation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    slot_code VARCHAR(50) NOT NULL,
    content_id BIGINT DEFAULT NULL,
    category_id BIGINT DEFAULT NULL,
    title_override VARCHAR(255) DEFAULT NULL,
    image_override VARCHAR(500) DEFAULT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    start_at DATETIME DEFAULT NULL,
    end_at DATETIME DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_home_slot (slot_code, status, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS search_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(64) NOT NULL,
    user_id BIGINT DEFAULT NULL,
    session_id VARCHAR(100) DEFAULT NULL,
    original_keyword VARCHAR(255) DEFAULT NULL,
    normalized_keyword VARCHAR(255) DEFAULT NULL,
    content_type VARCHAR(30) DEFAULT NULL,
    category_id BIGINT DEFAULT NULL,
    result_count INT NOT NULL DEFAULT 0,
    response_time_ms INT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_search_request_id (request_id),
    KEY idx_search_keyword (normalized_keyword),
    KEY idx_search_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS search_click_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(64) NOT NULL,
    user_id BIGINT DEFAULT NULL,
    session_id VARCHAR(100) DEFAULT NULL,
    content_id BIGINT NOT NULL,
    result_position INT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_click_request (request_id),
    KEY idx_click_content (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_content_favorite (user_id, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_browse_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL,
    session_id VARCHAR(100) DEFAULT NULL,
    content_id BIGINT NOT NULL,
    browse_count INT NOT NULL DEFAULT 1,
    first_browsed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_browsed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_browse_user_time (user_id, last_browsed_at),
    KEY idx_browse_session_time (session_id, last_browsed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feedback_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL,
    session_id VARCHAR(100) DEFAULT NULL,
    content VARCHAR(1000) NOT NULL,
    contact VARCHAR(100) DEFAULT NULL,
    page_path VARCHAR(255) DEFAULT NULL,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 管理后台扩展字段：处理状态、管理员备注、处理人、处理时间
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_note VARCHAR(1000) DEFAULT NULL,
    handled_by BIGINT DEFAULT NULL,
    handled_at DATETIME DEFAULT NULL,
    KEY idx_feedback_user_time (user_id, submitted_at),
    KEY idx_feedback_session_time (session_id, submitted_at),
    KEY idx_feedback_status (status, submitted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 反馈记录表幂等迁移：v0.1 -> v0.2 升级 feedback_record 表（CREATE TABLE IF NOT EXISTS 不会补字段）
-- 使用 information_schema + PREPARE/EXECUTE 保证重复执行幂等
SET @feedback_status_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'feedback_record'
      AND COLUMN_NAME = 'status'
);
SET @feedback_migrate_sql := IF(@feedback_status_exists = 0,
    'ALTER TABLE feedback_record ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT ''PENDING'', ADD COLUMN admin_note VARCHAR(1000) DEFAULT NULL, ADD COLUMN handled_by BIGINT DEFAULT NULL, ADD COLUMN handled_at DATETIME DEFAULT NULL, ADD KEY idx_feedback_status (status, submitted_at)',
    'SELECT 1');
PREPARE feedback_migrate_stmt FROM @feedback_migrate_sql;
EXECUTE feedback_migrate_stmt;
DEALLOCATE PREPARE feedback_migrate_stmt;
