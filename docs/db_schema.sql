-- 1. 用户表 (核心：存储港校身份核验状态)
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `openid` VARCHAR(128) NOT NULL COMMENT '微信用户唯一标识',
    `email_prefix` VARCHAR(64) DEFAULT NULL COMMENT 'CityU 邮箱前缀 (如 yuqianzhou)',
    `full_email` VARCHAR(128) DEFAULT NULL COMMENT '完整 CityU 邮箱地址',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    UNIQUE KEY `uk_full_email` (`full_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. 面经知识库 (核心：RAG 检索的数据源)
CREATE TABLE `interview_experiences` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `company_name` VARCHAR(100) NOT NULL,
    `job_title` VARCHAR(100) NOT NULL, -- 岗位：如 PM, BDA
    `tags` VARCHAR(255),               -- 标签：如 #SQL #BusinessLogic
    `content` TEXT NOT NULL,           -- 面经正文
    `difficulty_level` INT,            -- 难度评分
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. AI 诊断记录表 (核心：体现 BDA 逻辑和结果持久化)
CREATE TABLE `ai_analysis_reports` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT,
    `job_id` BIGINT,
    `match_score` DECIMAL(5,2),        -- 匹配得分，如 85.50
    `strengths` TEXT,                  -- 优势分析 (JSON格式存储)
    `weaknesses` TEXT,                 -- 劣势分析 (JSON格式存储)
    `suggested_questions` TEXT,        -- AI 生成的预测面试题
    `resume_snapshot` TEXT,            -- 当时分析的简历文本快照
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

-- 4. 现有职位表
CREATE TABLE `jobs` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `title` VARCHAR(100) NOT NULL COMMENT '职位名称',
                        `company` VARCHAR(100) NOT NULL COMMENT '公司名称',
                        `salary` VARCHAR(50) COMMENT '薪资范围',
                        `description` TEXT COMMENT '职位描述',
                        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
