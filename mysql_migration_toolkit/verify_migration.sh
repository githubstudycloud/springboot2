#!/bin/bash

# MySQL迁移验证脚本
# 用于验证源数据库和目标数据库中表的一致性

# 配置信息 - 请根据实际环境修改
SOURCE_USER="root"
SOURCE_PASS="password"
SOURCE_HOST="localhost"
SOURCE_PORT="3306"
TARGET_USER="root"
TARGET_PASS="password"
TARGET_HOST="target-host"
TARGET_PORT="3306"
TABLES_LIST_FILE="./output/tables_to_migrate.csv"  # 表名列表文件
VERIFY_DIR="./verify"                             # 验证结果目录
VERIFY_DB="db_migration_verify"                   # 验证数据库名

# 检查输入文件是否存在
if [ ! -f "$TABLES_LIST_FILE" ]; then
  echo "错误: 表名列表文件 '$TABLES_LIST_FILE' 不存在!"
  exit 1
fi

# 创建验证结果目录
mkdir -p $VERIFY_DIR

echo "开始验证迁移结果..."

# 创建源库表信息文件
SOURCE_INFO_FILE="$VERIFY_DIR/source_tables_info.csv"
echo "数据库,表名,行数,数据大小,索引大小,总大小" > $SOURCE_INFO_FILE

# 创建目标库表信息文件
TARGET_INFO_FILE="$VERIFY_DIR/target_tables_info.csv"
echo "数据库,表名,行数,数据大小,索引大小,总大小" > $TARGET_INFO_FILE

# 从源库收集表信息
echo "正在从源库收集表信息..."
cat $TABLES_LIST_FILE | while IFS=, read DB_NAME TABLE_NAME PRIORITY; do
  # 跳过CSV文件的标题行（如果有）
  if [ "$DB_NAME" = "db_name" ]; then
    continue
  fi
  
  echo "  收集 $DB_NAME.$TABLE_NAME 信息..."
  
  # 获取表信息
  INFO=$(mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS -e "
    SELECT 
      TABLE_ROWS as row_count,
      DATA_LENGTH as data_size,
      INDEX_LENGTH as index_size,
      DATA_LENGTH + INDEX_LENGTH as total_size
    FROM 
      information_schema.tables 
    WHERE 
      table_schema='$DB_NAME' AND table_name='$TABLE_NAME';" -s)
  
  ROW_COUNT=$(echo "$INFO" | awk '{print $1}')
  DATA_SIZE=$(echo "$INFO" | awk '{print $2}')
  INDEX_SIZE=$(echo "$INFO" | awk '{print $3}')
  TOTAL_SIZE=$(echo "$INFO" | awk '{print $4}')
  
  # 写入CSV文件
  echo "$DB_NAME,$TABLE_NAME,$ROW_COUNT,$DATA_SIZE,$INDEX_SIZE,$TOTAL_SIZE" >> $SOURCE_INFO_FILE
done

# 从目标库收集表信息
echo "正在从目标库收集表信息..."
cat $TABLES_LIST_FILE | while IFS=, read DB_NAME TABLE_NAME PRIORITY; do
  # 跳过CSV文件的标题行（如果有）
  if [ "$DB_NAME" = "db_name" ]; then
    continue
  fi
  
  echo "  收集 $DB_NAME.$TABLE_NAME 信息..."
  
  # 获取表信息
  INFO=$(mysql -h$TARGET_HOST -P$TARGET_PORT -u$TARGET_USER -p$TARGET_PASS -e "
    SELECT 
      TABLE_ROWS as row_count,
      DATA_LENGTH as data_size,
      INDEX_LENGTH as index_size,
      DATA_LENGTH + INDEX_LENGTH as total_size
    FROM 
      information_schema.tables 
    WHERE 
      table_schema='$DB_NAME' AND table_name='$TABLE_NAME';" -s)
  
  ROW_COUNT=$(echo "$INFO" | awk '{print $1}')
  DATA_SIZE=$(echo "$INFO" | awk '{print $2}')
  INDEX_SIZE=$(echo "$INFO" | awk '{print $3}')
  TOTAL_SIZE=$(echo "$INFO" | awk '{print $4}')
  
  # 写入CSV文件
  echo "$DB_NAME,$TABLE_NAME,$ROW_COUNT,$DATA_SIZE,$INDEX_SIZE,$TOTAL_SIZE" >> $TARGET_INFO_FILE
done

# 创建验证数据库
echo "创建验证数据库..."
mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS -e "
CREATE DATABASE IF NOT EXISTS $VERIFY_DB;

USE $VERIFY_DB;

-- 创建源表信息表
DROP TABLE IF EXISTS source_tables_info;
CREATE TABLE source_tables_info (
  id INT AUTO_INCREMENT PRIMARY KEY,
  db_name VARCHAR(100) NOT NULL,
  table_name VARCHAR(100) NOT NULL,
  row_count BIGINT NOT NULL,
  data_size BIGINT NOT NULL,
  index_size BIGINT NOT NULL,
  total_size BIGINT NOT NULL,
  UNIQUE KEY (db_name, table_name)
);

-- 创建目标表信息表
DROP TABLE IF EXISTS target_tables_info;
CREATE TABLE target_tables_info (
  id INT AUTO_INCREMENT PRIMARY KEY,
  db_name VARCHAR(100) NOT NULL,
  table_name VARCHAR(100) NOT NULL,
  row_count BIGINT NOT NULL,
  data_size BIGINT NOT NULL,
  index_size BIGINT NOT NULL,
  total_size BIGINT NOT NULL,
  UNIQUE KEY (db_name, table_name)
);

-- 创建比较结果表
DROP TABLE IF EXISTS comparison_results;
CREATE TABLE comparison_results (
  id INT AUTO_INCREMENT PRIMARY KEY,
  db_name VARCHAR(100) NOT NULL,
  table_name VARCHAR(100) NOT NULL,
  source_rows BIGINT,
  target_rows BIGINT,
  source_size BIGINT,
  target_size BIGINT,
  row_diff BIGINT,
  size_diff BIGINT,
  diff_percentage DECIMAL(5,2),
  status ENUM('一致', '不一致', '缺失') NOT NULL
);
"

# 导入信息到验证数据库
echo "导入表信息到验证数据库..."
mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS $VERIFY_DB -e "
LOAD DATA INFILE '$SOURCE_INFO_FILE'
INTO TABLE source_tables_info
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(db_name, table_name, row_count, data_size, index_size, total_size);

LOAD DATA INFILE '$TARGET_INFO_FILE'
INTO TABLE target_tables_info
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(db_name, table_name, row_count, data_size, index_size, total_size);
"

# 生成比较结果
echo "生成比较结果..."
mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS $VERIFY_DB -e "
-- 插入两边都有的表
INSERT INTO comparison_results (
  db_name, table_name, 
  source_rows, target_rows, 
  source_size, target_size,
  row_diff, size_diff, diff_percentage, status
)
SELECT
  s.db_name, s.table_name,
  s.row_count, t.row_count,
  s.total_size, t.total_size,
  ABS(s.row_count - t.row_count) as row_diff,
  ABS(s.total_size - t.total_size) as size_diff,
  CASE 
    WHEN s.total_size = 0 THEN 0
    ELSE ROUND(ABS(s.total_size - t.total_size) / s.total_size * 100, 2)
  END as diff_percentage,
  CASE
    WHEN ABS(s.row_count - t.row_count) <= s.row_count * 0.01 AND 
         ABS(s.total_size - t.total_size) <= s.total_size * 0.05 THEN '一致'
    ELSE '不一致'
  END as status
FROM
  source_tables_info s
JOIN
  target_tables_info t ON s.db_name = t.db_name AND s.table_name = t.table_name;

-- 插入源有但目标没有的表
INSERT INTO comparison_results (
  db_name, table_name,
  source_rows, target_rows,
  source_size, target_size,
  row_diff, size_diff, diff_percentage, status
)
SELECT
  s.db_name, s.table_name,
  s.row_count, 0,
  s.total_size, 0,
  s.row_count, s.total_size, 100, '缺失'
FROM
  source_tables_info s
LEFT JOIN
  target_tables_info t ON s.db_name = t.db_name AND s.table_name = t.table_name
WHERE
  t.id IS NULL;

-- 导出比较结果
SELECT * FROM comparison_results
ORDER BY status, db_name, table_name;
" > "$VERIFY_DIR/comparison_results.txt"

# 导出不一致的表
mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS $VERIFY_DB -e "
SELECT db_name, table_name, source_rows, target_rows, row_diff, diff_percentage, status
FROM comparison_results
WHERE status != '一致'
ORDER BY db_name, table_name;" > "$VERIFY_DIR/inconsistent_tables.txt"

# 导出CSV格式的比较结果
mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS $VERIFY_DB -e "
SELECT db_name, table_name, source_rows, target_rows, row_diff, 
       source_size, target_size, size_diff, diff_percentage, status
FROM comparison_results
ORDER BY status, db_name, table_name;" | sed 's/\t/,/g' > "$VERIFY_DIR/comparison_results.csv"

echo "验证完成！"
echo "比较结果已保存到: $VERIFY_DIR/comparison_results.txt"
echo "不一致的表已保存到: $VERIFY_DIR/inconsistent_tables.txt"
echo "CSV格式的比较结果已保存到: $VERIFY_DIR/comparison_results.csv"

# 统计一致性情况
TOTAL=$(grep -c . "$VERIFY_DIR/comparison_results.csv")
INCONSISTENT=$(grep -c "不一致\|缺失" "$VERIFY_DIR/inconsistent_tables.txt")
CONSISTENT=$((TOTAL - INCONSISTENT - 1))  # 减1是因为CSV标题行

echo "统计结果:"
echo "  总表数: $((TOTAL - 1))"  # 减1是因为CSV标题行
echo "  一致的表: $CONSISTENT"
echo "  不一致或缺失的表: $INCONSISTENT"

if [ $INCONSISTENT -eq 0 ]; then
  echo "恭喜！所有表迁移一致性验证通过！"
else
  echo "警告：发现 $INCONSISTENT 个表存在一致性问题，请检查 $VERIFY_DIR/inconsistent_tables.txt"
fi
