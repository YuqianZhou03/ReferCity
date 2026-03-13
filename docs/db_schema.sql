-- 1. 用户表 (核心：存储港校身份核验状态)
CREATE TABLE `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) UNIQUE, -- 用于校友邮箱验证
    `is_verified` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
