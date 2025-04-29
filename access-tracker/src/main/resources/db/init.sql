-- Database schema for access tracking

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS access_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE access_tracker;

-- Table for last access records
CREATE TABLE IF NOT EXISTS last_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_user VARCHAR(100) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    access_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    UNIQUE KEY uk_log_user (log_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table for history access records (one per user per day)
CREATE TABLE IF NOT EXISTS history_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_user VARCHAR(100) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    access_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    UNIQUE KEY uk_log_user_date (log_user, access_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add indexes
CREATE INDEX idx_history_access_date ON history_access (access_date);
CREATE INDEX idx_history_access_log_user ON history_access (log_user);
