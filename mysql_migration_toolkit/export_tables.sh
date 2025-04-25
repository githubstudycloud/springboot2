#!/bin/bash

# MySQL表导出脚本
# 根据筛选后的表名列表，导出表结构和数据

# 配置信息 - 请根据实际环境修改
SOURCE_USER="root"
SOURCE_PASS="password"
SOURCE_HOST="localhost"
SOURCE_PORT="3306"
TABLES_LIST_FILE="./output/tables_to_migrate.csv"  # 表名列表文件
EXPORT_DIR="./export"                              # 导出目录

# 检查输入文件是否存在
if [ ! -f "$TABLES_LIST_FILE" ]; then
  echo "错误: 表名列表文件 '$TABLES_LIST_FILE' 不存在!"
  echo "请先完成人工筛选并导出需要迁移的表名列表。"
  exit 1
fi

# 创建导出目录
mkdir -p $EXPORT_DIR

echo "开始导出表结构和数据..."

# 按数据库分组创建子目录
cat $TABLES_LIST_FILE | while IFS=, read DB_NAME TABLE_NAME PRIORITY; do
  # 跳过CSV文件的标题行（如果有）
  if [ "$DB_NAME" = "db_name" ]; then
    continue
  fi
  
  # 创建数据库子目录
  DB_DIR="$EXPORT_DIR/$DB_NAME"
  mkdir -p $DB_DIR
  
  # 记录已处理的数据库，用于后面生成合并文件
  echo $DB_NAME >> $EXPORT_DIR/processed_dbs.txt
done

# 确保processed_dbs.txt存在
touch $EXPORT_DIR/processed_dbs.txt

# 获取唯一的数据库列表
UNIQUE_DBS=$(sort -u $EXPORT_DIR/processed_dbs.txt)

# 为每个数据库导出所有表结构（一次性）
for DB in $UNIQUE_DBS; do
  echo "正在导出数据库 $DB 的表结构..."
  DB_DIR="$EXPORT_DIR/$DB"
  
  # 导出所有表结构（不含数据）
  mysqldump -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS \
    --no-data --routines --triggers --events \
    $DB > "$DB_DIR/all_structures.sql"
  
  # 创建数据导出的头文件（包含USE语句）
  echo "-- 数据库 $DB 的数据导出" > "$DB_DIR/all_data.sql"
  echo "USE \`$DB\`;" >> "$DB_DIR/all_data.sql"
  echo >> "$DB_DIR/all_data.sql"
done

# 逐个导出表数据
echo "开始逐个导出表数据..."
cat $TABLES_LIST_FILE | while IFS=, read DB_NAME TABLE_NAME PRIORITY; do
  # 跳过CSV文件的标题行（如果有）
  if [ "$DB_NAME" = "db_name" ]; then
    continue
  fi
  
  echo "正在导出 $DB_NAME.$TABLE_NAME (优先级: $PRIORITY)..."
  DB_DIR="$EXPORT_DIR/$DB_NAME"
  
  # 导出单表结构（可选，因为已经在all_structures.sql中包含）
  mysqldump -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS \
    --no-data $DB_NAME $TABLE_NAME > "$DB_DIR/${TABLE_NAME}_structure.sql"
  
  # 导出表数据
  mysqldump -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS \
    --single-transaction --quick --no-create-info \
    $DB_NAME $TABLE_NAME > "$DB_DIR/${TABLE_NAME}_data.sql"
  
  # 将表数据追加到合并文件
  cat "$DB_DIR/${TABLE_NAME}_data.sql" >> "$DB_DIR/all_data.sql"
  echo >> "$DB_DIR/all_data.sql"
  
  # 创建导出完成标记
  touch "$DB_DIR/${TABLE_NAME}_exported"
done

# 删除临时文件
rm -f $EXPORT_DIR/processed_dbs.txt

echo "导出完成！"
echo "所有导出文件已保存到 $EXPORT_DIR 目录"
echo "现在您可以使用Navicat导入这些SQL文件到目标服务器"
echo ""
echo "提示: 推荐的导入顺序"
echo "1. 首先导入各数据库的all_structures.sql文件（表结构）"
echo "2. 然后导入各数据库的all_data.sql文件（表数据）"
echo "  或者按表的优先级分批导入各表的_data.sql文件"
