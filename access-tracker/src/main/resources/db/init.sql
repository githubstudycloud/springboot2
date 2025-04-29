-- Database schema for access tracking

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS access_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE access_tracker;

-- Drop tables if they exist to ensure clean schema
DROP TABLE IF EXISTS history_access;
DROP TABLE IF EXISTS last_access;

-- Table for last access records
CREATE TABLE IF NOT EXISTS last_access (
    id BIGINT AUTO_INCREMENT,
    log_user VARCHAR(100) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    project_id VARCHAR(100) NOT NULL,
    versionpbi VARCHAR(100) NOT NULL,
    access_date DATE NOT NULL,
    first_access_time DATETIME NOT NULL,
    last_access_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_last_access (log_user, project_id, versionpbi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table for history access records (one per user per day per project)
CREATE TABLE IF NOT EXISTS history_access (
    id BIGINT AUTO_INCREMENT,
    log_user VARCHAR(100) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    project_id VARCHAR(100) NOT NULL,
    versionpbi VARCHAR(100) NOT NULL,
    access_date DATE NOT NULL,
    first_access_time DATETIME NOT NULL,
    last_access_time DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_history_access (log_user, project_id, versionpbi, access_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add indexes
CREATE INDEX idx_history_access_date ON history_access (access_date);
CREATE INDEX idx_history_access_log_user ON history_access (log_user);
CREATE INDEX idx_history_access_project ON history_access (project_id);
CREATE INDEX idx_last_access_log_user ON last_access (log_user);
CREATE INDEX idx_last_access_project ON last_access (project_id);
