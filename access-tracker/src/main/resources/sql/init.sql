-- Create database
CREATE DATABASE IF NOT EXISTS access_tracker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE access_tracker;

-- Create last_access table
CREATE TABLE IF NOT EXISTS last_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_user VARCHAR(100) NOT NULL COMMENT 'User ID or username',
    uuid VARCHAR(36) NOT NULL COMMENT 'Unique identifier for the visit session',
    access_date DATE NOT NULL COMMENT 'Visit date (year, month, day)',
    start_time TIME NOT NULL COMMENT 'Start time of the visit',
    end_time TIME NOT NULL COMMENT 'End time of the visit',
    UNIQUE KEY idx_user (log_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Last access information for each user';

-- Create history_access table
CREATE TABLE IF NOT EXISTS history_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_user VARCHAR(100) NOT NULL COMMENT 'User ID or username',
    uuid VARCHAR(36) NOT NULL COMMENT 'Unique identifier for the visit session',
    access_date DATE NOT NULL COMMENT 'Visit date (year, month, day)',
    start_time TIME NOT NULL COMMENT 'Start time of the first visit of the day',
    end_time TIME NOT NULL COMMENT 'End time of the first visit of the day',
    UNIQUE KEY idx_user_date (log_user, access_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='History access information, one record per user per day';

-- Create indexes
CREATE INDEX idx_history_date ON history_access (access_date);
CREATE INDEX idx_history_user ON history_access (log_user);
