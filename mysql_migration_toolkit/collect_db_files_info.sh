#!/bin/bash

# MySQL数据库文件信息收集脚本
# 用于收集数据库文件的路径、大小、修改时间等信息，并生成CSV文件

# 配置信息 - 请根据实际环境修改
MYSQL_DATA_DIR="/var/lib/mysql"  # MySQL数据目录
OUTPUT_DIR="./output"            # 输出目录
OUTPUT_FILE="$OUTPUT_DIR/mysql_files_info.csv"  # 输出文件
EXCLUDE_DBS="information_schema performance_schema mysql sys"  # 排除的系统数据库

# 创建输出目录
mkdir -p $OUTPUT_DIR

# 检查MySQL数据目录是否存在
if [ ! -d "$MYSQL_DATA_DIR" ]; then
  echo "错误: MySQL数据目录 '$MYSQL_DATA_DIR' 不存在!"
  echo "请修改脚本中的MYSQL_DATA_DIR变量为正确的路径。"
  exit 1
fi

echo "开始收集MySQL数据文件信息..."
echo "MySQL数据目录: $MYSQL_DATA_DIR"

# 创建CSV文件头
echo "文件路径,数据库名,表名,文件大小(bytes),修改时间,是否需迁移" > $OUTPUT_FILE

# 使用find命令查找所有.ibd文件（InnoDB表空间文件）
find $MYSQL_DATA_DIR -name "*.ibd" -type f | sort | while read -r FILE_PATH; do
  # 计算文件大小
  FILE_SIZE=$(stat -c "%s" "$FILE_PATH")
  
  # 获取文件修改时间
  MODIFY_TIME=$(stat -c "%y" "$FILE_PATH" | cut -d. -f1)
  
  # 从路径中提取数据库名和表名
  REL_PATH=${FILE_PATH#$MYSQL_DATA_DIR}
  REL_PATH=${REL_PATH#/}
  
  # 第一级目录是数据库名
  DB_NAME=$(echo $REL_PATH | cut -d'/' -f1)
  
  # 跳过系统数据库
  if echo "$EXCLUDE_DBS" | grep -q "$DB_NAME"; then
    continue
  fi
  
  # 文件名（不含.ibd扩展名）是表名
  TABLE_NAME=$(basename "$FILE_PATH" .ibd)
  
  # 写入CSV文件
  echo "$FILE_PATH,$DB_NAME,$TABLE_NAME,$FILE_SIZE,$MODIFY_TIME,0" >> $OUTPUT_FILE
done

# 按修改时间排序（从新到旧）
TMP_FILE=$(mktemp)
head -1 $OUTPUT_FILE > $TMP_FILE  # 保留表头
tail -n +2 $OUTPUT_FILE | sort -t, -k5 -r >> $TMP_FILE  # 按第5列（修改时间）降序排序
mv $TMP_FILE $OUTPUT_FILE

echo "数据收集完成！"
echo "共收集了 $(wc -l < $OUTPUT_FILE) 个文件的信息（包含表头）"
echo "结果已保存到 $OUTPUT_FILE"
echo ""
echo "下一步: 使用analyze_table_usage.py分析表使用情况，或直接将数据导入到MySQL临时表"
