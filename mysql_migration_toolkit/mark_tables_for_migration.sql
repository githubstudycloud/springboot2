-- MySQL表筛选和标记脚本
-- 用于人工筛选需要迁移的表

USE db_migration_temp;

-- 创建数据迁移决策视图
CREATE OR REPLACE VIEW v_migration_decision AS
SELECT 
  id,
  db_name AS '数据库名',
  table_name AS '表名',
  ROUND(file_size/1024/1024, 2) AS '文件大小(MB)',
  modify_time AS '最后修改时间',
  java_references + xml_references AS '代码引用总数',
  select_count AS 'SELECT次数',
  insert_count AS 'INSERT次数',
  update_count AS 'UPDATE次数',
  delete_count AS 'DELETE次数',
  CASE 
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 7 DAY) THEN '最近7天'
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 30 DAY) THEN '最近30天'
    WHEN modify_time > DATE_SUB(NOW(), INTERVAL 90 DAY) THEN '最近90天'
    ELSE '更早'
  END AS '修改时间分类',
  CASE 
    WHEN insert_count > 0 OR update_count > 0 OR delete_count > 0 THEN '是'
    ELSE '否'
  END AS '存在写操作',
  CASE 
    WHEN need_migrate = 1 THEN '是'
    ELSE '否'
  END AS '需要迁移',
  migration_priority AS '迁移优先级',
  notes AS '备注'
FROM 
  tmp_migration_files
ORDER BY 
  modify_time DESC;

-- 查看所有表的迁移决策信息
SELECT * FROM v_migration_decision;

-- 查看按修改时间分组的表数量统计
SELECT 
  `修改时间分类`, 
  COUNT(*) AS '表数量', 
  SUM(CASE WHEN `需要迁移` = '是' THEN 1 ELSE 0 END) AS '已标记迁移的表数量',
  ROUND(AVG(`文件大小(MB)`), 2) AS '平均大小(MB)'
FROM 
  v_migration_decision
GROUP BY 
  `修改时间分类`
ORDER BY 
  CASE 
    WHEN `修改时间分类` = '最近7天' THEN 1
    WHEN `修改时间分类` = '最近30天' THEN 2
    WHEN `修改时间分类` = '最近90天' THEN 3
    ELSE 4
  END;

-- 查看有写操作的表统计
SELECT 
  `存在写操作`, 
  COUNT(*) AS '表数量', 
  SUM(CASE WHEN `需要迁移` = '是' THEN 1 ELSE 0 END) AS '已标记迁移的表数量'
FROM 
  v_migration_decision
GROUP BY 
  `存在写操作`;

-- 用于标记需要迁移的表的辅助语句（根据表名）
-- 将下面的示例表名替换为实际需要标记的表名
/*
UPDATE tmp_migration_files
SET need_migrate = 1, migration_priority = 1
WHERE table_name IN ('table1', 'table2', 'table3');
*/

-- 用于批量标记最近修改且有写操作的表
/*
UPDATE tmp_migration_files
SET need_migrate = 1, migration_priority = 1
WHERE 
  modify_time > DATE_SUB(NOW(), INTERVAL 30 DAY) AND
  (insert_count > 0 OR update_count > 0 OR delete_count > 0);
*/

-- 用于批量标记有高代码引用次数的表
/*
UPDATE tmp_migration_files
SET need_migrate = 1, migration_priority = 2
WHERE 
  (java_references + xml_references) > 10 AND
  need_migrate = 0;
*/

-- 导出需要迁移的表名列表
-- 完成标记后取消下面注释并执行
/*
SELECT 
  db_name, table_name, migration_priority
FROM 
  tmp_migration_files
WHERE 
  need_migrate = 1
ORDER BY 
  migration_priority, db_name, table_name
INTO OUTFILE '/path/to/output/tables_to_migrate.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';
*/
