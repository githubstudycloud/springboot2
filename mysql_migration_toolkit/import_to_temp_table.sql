-- MySQL临时表导入脚本
-- 用于创建临时表并导入数据库文件信息和表使用情况分析结果

-- 创建临时数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS db_migration_temp;

USE db_migration_temp;

-- 创建临时表
DROP TABLE IF EXISTS tmp_migration_files;
CREATE TABLE tmp_migration_files (
  id INT AUTO_INCREMENT PRIMARY KEY,
  file_path VARCHAR(255) NOT NULL,
  db_name VARCHAR(100) NOT NULL,
  table_name VARCHAR(100) NOT NULL,
  file_size BIGINT NOT NULL,
  modify_time DATETIME NOT NULL,
  java_references INT DEFAULT 0,
  xml_references INT DEFAULT 0,
  select_count INT DEFAULT 0,
  insert_count INT DEFAULT 0,
  update_count INT DEFAULT 0,
  delete_count INT DEFAULT 0,
  need_migrate TINYINT DEFAULT 0 COMMENT '0-不迁移, 1-需迁移',
  migration_priority INT DEFAULT 0 COMMENT '迁移优先级,越小越优先',
  notes TEXT DEFAULT NULL COMMENT '备注信息'
);

-- 创建数据导入表
DROP TABLE IF EXISTS tmp_file_info;
CREATE TEMPORARY TABLE tmp_file_info (
  file_path VARCHAR(255) NOT NULL,
  db_name VARCHAR(100) NOT NULL,
  table_name VARCHAR(100) NOT NULL,
  file_size BIGINT NOT NULL,
  modify_time VARCHAR(50) NOT NULL,
  need_migrate TINYINT DEFAULT 0
);

-- 创建表使用情况导入表
DROP TABLE IF EXISTS tmp_table_usage;
CREATE TEMPORARY TABLE tmp_table_usage (
  table_name VARCHAR(100) NOT NULL,
  java_references INT DEFAULT 0,
  xml_references INT DEFAULT 0,
  select_count INT DEFAULT 0,
  insert_count INT DEFAULT 0,
  update_count INT DEFAULT 0,
  delete_count INT DEFAULT 0,
  referenced_files TEXT
);

-- 导入数据库文件信息（需要替换文件路径）
LOAD DATA INFILE '/path/to/mysql_files_info.csv'
INTO TABLE tmp_file_info
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(file_path, db_name, table_name, file_size, modify_time, need_migrate);

-- 导入表使用情况分析结果（需要替换文件路径）
LOAD DATA INFILE '/path/to/table_usage_analysis.csv'
INTO TABLE tmp_table_usage
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(table_name, java_references, xml_references, select_count, insert_count, update_count, delete_count, referenced_files);

-- 合并数据到临时表
INSERT INTO tmp_migration_files (
  file_path, db_name, table_name, file_size, modify_time, 
  java_references, xml_references, select_count, insert_count, update_count, delete_count, 
  need_migrate, migration_priority
)
SELECT 
  f.file_path, f.db_name, f.table_name, f.file_size, 
  STR_TO_DATE(f.modify_time, '%Y-%m-%d %H:%i:%S'),
  COALESCE(u.java_references, 0),
  COALESCE(u.xml_references, 0),
  COALESCE(u.select_count, 0),
  COALESCE(u.insert_count, 0),
  COALESCE(u.update_count, 0),
  COALESCE(u.delete_count, 0),
  0, -- 默认不迁移，需人工标记
  0  -- 默认优先级为0
FROM 
  tmp_file_info f
LEFT JOIN 
  tmp_table_usage u ON f.table_name = u.table_name;

-- 清理临时表
DROP TEMPORARY TABLE tmp_file_info;
DROP TEMPORARY TABLE tmp_table_usage;

-- 根据修改时间和引用情况设置初始的迁移建议
-- 近期修改且有写操作的表，优先级更高
UPDATE tmp_migration_files
SET 
  need_migrate = CASE 
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 30 DAY) AND (insert_count > 0 OR update_count > 0 OR delete_count > 0) THEN 1
    ELSE 0
  END,
  migration_priority = CASE
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 2
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 90 DAY) THEN 3
    ELSE 4
  END;

-- 为需要人工筛选的表创建视图
CREATE OR REPLACE VIEW v_tables_to_review AS
SELECT 
  id, db_name, table_name, file_size, modify_time,
  (java_references + xml_references) AS total_references,
  select_count, insert_count, update_count, delete_count,
  need_migrate, migration_priority, notes
FROM 
  tmp_migration_files
ORDER BY 
  migration_priority, modify_time DESC;

-- 显示需要审查的表
SELECT * FROM v_tables_to_review;

-- 人工标记后导出需要迁移的表
-- 完成标记后运行以下SQL导出表名列表
/*
SELECT 
  db_name, table_name, migration_priority
FROM 
  tmp_migration_files
WHERE 
  need_migrate = 1
ORDER BY 
  migration_priority, db_name, table_name
INTO OUTFILE '/path/to/tables_to_migrate.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';
*/
