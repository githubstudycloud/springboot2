#!/bin/bash

# MySQL迁移工具包配置文件
# 在此设置所有脚本共用的配置参数

# 数据库连接信息
# 源数据库（当前数据库）
SOURCE_USER="root"
SOURCE_PASS="password"
SOURCE_HOST="localhost"
SOURCE_PORT="3306"

# 目标数据库（要迁移到的新数据库）
TARGET_USER="root"
TARGET_PASS="password"
TARGET_HOST="target-host"
TARGET_PORT="3306"

# MySQL数据目录
MYSQL_DATA_DIR="/var/lib/mysql"

# 项目代码目录（多个目录用空格分隔）
PROJECT_DIRS="/path/to/project1 /path/to/project2"

# 输出和临时目录
OUTPUT_DIR="./output"
EXPORT_DIR="./export"
VERIFY_DIR="./verify"

# 临时数据库名
TEMP_DB="db_migration_temp"
VERIFY_DB="db_migration_verify"

# 表名列表文件
TABLES_LIST_FILE="$OUTPUT_DIR/tables_to_migrate.csv"

# 是否排除系统数据库（1=是，0=否）
EXCLUDE_SYSTEM_DBS=1

# 系统数据库列表
SYSTEM_DBS="information_schema performance_schema mysql sys"

# 调试模式（1=开启，0=关闭）
DEBUG_MODE=0

# 创建必要的目录
mkdir -p $OUTPUT_DIR
mkdir -p $EXPORT_DIR
mkdir -p $VERIFY_DIR

# 输出配置信息
if [ "$DEBUG_MODE" -eq 1 ]; then
  echo "配置信息:"
  echo "源数据库: $SOURCE_USER@$SOURCE_HOST:$SOURCE_PORT"
  echo "目标数据库: $TARGET_USER@$TARGET_HOST:$TARGET_PORT"
  echo "MySQL数据目录: $MYSQL_DATA_DIR"
  echo "项目代码目录: $PROJECT_DIRS"
  echo "输出目录: $OUTPUT_DIR"
  echo "导出目录: $EXPORT_DIR"
  echo "验证目录: $VERIFY_DIR"
fi
