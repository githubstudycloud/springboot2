#!/bin/bash

# MySQL数据库增量迁移工具包主入口脚本
# 用于协调各个迁移阶段的执行

# 载入配置
source ./config.sh

# 显示菜单
show_menu() {
  clear
  echo "======================= MySQL 增量迁移工具包 ======================="
  echo "1. 收集数据库文件信息"
  echo "2. 分析表在项目代码中的使用情况"
  echo "3. 创建临时表并导入分析数据"
  echo "4. 标记需要迁移的表（需手动操作）"
  echo "5. 导出选定的表"
  echo "6. 验证迁移结果"
  echo "7. 清理临时文件"
  echo "8. 执行完整迁移流程（1-5步）"
  echo "0. 退出"
  echo "=================================================================="
  echo -n "请选择操作 [0-8]: "
}

# 收集数据库文件信息
collect_db_info() {
  echo "开始收集数据库文件信息..."
  
  # 将配置传递给脚本
  export MYSQL_DATA_DIR
  export OUTPUT_DIR
  export EXCLUDE_SYSTEM_DBS
  export SYSTEM_DBS
  
  # 执行收集脚本
  bash ./collect_db_files_info.sh
  
  echo "数据库文件信息收集完成!"
  read -p "按任意键继续..." key
}

# 分析表使用情况
analyze_table_usage() {
  echo "开始分析表在项目代码中的使用情况..."
  
  # 检查Python是否安装
  if ! command -v python3 &> /dev/null; then
    echo "错误: 未找到Python3，请安装后再试!"
    read -p "按任意键继续..." key
    return
  fi
  
  # 检查输入文件是否存在
  if [ ! -f "$OUTPUT_DIR/mysql_files_info.csv" ]; then
    echo "错误: 未找到数据库文件信息CSV，请先执行步骤1!"
    read -p "按任意键继续..." key
    return
  fi
  
  # 检查项目目录是否存在
  for dir in $PROJECT_DIRS; do
    if [ ! -d "$dir" ]; then
      echo "警告: 项目目录 '$dir' 不存在!"
      read -p "是否继续? [y/n]: " cont
      if [ "$cont" != "y" ]; then
        return
      fi
      break
    fi
  done
  
  # 执行Python分析脚本
  python3 ./analyze_table_usage.py -i "$OUTPUT_DIR/mysql_files_info.csv" -p $PROJECT_DIRS -o "$OUTPUT_DIR"
  
  echo "表使用情况分析完成!"
  read -p "按任意键继续..." key
}

# 创建临时表并导入数据
import_to_temp_table() {
  echo "开始创建临时表并导入数据..."
  
  # 检查输入文件是否存在
  if [ ! -f "$OUTPUT_DIR/mysql_files_info.csv" ]; then
    echo "错误: 未找到数据库文件信息CSV，请先执行步骤1!"
    read -p "按任意键继续..." key
    return
  fi
  
  if [ ! -f "$OUTPUT_DIR/table_usage_analysis.csv" ]; then
    echo "警告: 未找到表使用情况分析CSV，建议先执行步骤2!"
    read -p "是否继续? [y/n]: " cont
    if [ "$cont" != "y" ]; then
      return
    fi
  fi
  
  # 生成临时SQL文件
  TMP_SQL=$(mktemp)
  cat ./import_to_temp_table.sql > $TMP_SQL
  
  # 替换文件路径
  sed -i "s|/path/to/mysql_files_info.csv|$OUTPUT_DIR/mysql_files_info.csv|g" $TMP_SQL
  sed -i "s|/path/to/table_usage_analysis.csv|$OUTPUT_DIR/table_usage_analysis.csv|g" $TMP_SQL
  
  # 执行SQL脚本
  echo "执行SQL导入脚本..."
  mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS < $TMP_SQL
  
  # 清理临时文件
  rm -f $TMP_SQL
  
  echo "临时表创建和数据导入完成!"
  echo "请使用MySQL客户端手动标记需要迁移的表..."
  echo "使用以下命令连接到临时数据库:"
  echo "  mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS $TEMP_DB"
  echo "然后执行mark_tables_for_migration.sql中的SQL语句进行标记"
  
  read -p "按任意键继续..." key
}

# 导出选定的表
export_tables() {
  echo "开始导出选定的表..."
  
  # 检查表名列表是否存在
  if [ ! -f "$TABLES_LIST_FILE" ]; then
    echo "错误: 未找到要迁移的表名列表，请先执行步骤4!"
    read -p "按任意键继续..." key
    return
  fi
  
  # 将配置传递给脚本
  export SOURCE_USER
  export SOURCE_PASS
  export SOURCE_HOST
  export SOURCE_PORT
  export TABLES_LIST_FILE
  export EXPORT_DIR
  
  # 执行导出脚本
  bash ./export_tables.sh
  
  echo "表导出完成!"
  echo "导出文件保存在: $EXPORT_DIR"
  echo "请使用Navicat或其他工具将导出的SQL文件导入到目标数据库"
  
  read -p "按任意键继续..." key
}

# 验证迁移结果
verify_migration() {
  echo "开始验证迁移结果..."
  
  # 检查表名列表是否存在
  if [ ! -f "$TABLES_LIST_FILE" ]; then
    echo "错误: 未找到要迁移的表名列表，请先执行步骤4!"
    read -p "按任意键继续..." key
    return
  fi
  
  # 将配置传递给脚本
  export SOURCE_USER
  export SOURCE_PASS
  export SOURCE_HOST
  export SOURCE_PORT
  export TARGET_USER
  export TARGET_PASS
  export TARGET_HOST
  export TARGET_PORT
  export TABLES_LIST_FILE
  export VERIFY_DIR
  export VERIFY_DB
  
  # 执行验证脚本
  bash ./verify_migration.sh
  
  echo "迁移验证完成!"
  echo "验证结果保存在: $VERIFY_DIR"
  
  read -p "按任意键继续..." key
}

# 清理临时文件
cleanup() {
  echo "开始清理临时文件..."
  
  read -p "确定要删除所有临时数据库和文件吗? [y/n]: " confirm
  if [ "$confirm" = "y" ]; then
    # 删除临时数据库
    mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS -e "
    DROP DATABASE IF EXISTS $TEMP_DB;
    DROP DATABASE IF EXISTS $VERIFY_DB;"
    
    # 询问是否删除输出目录
    read -p "是否同时删除所有输出文件? [y/n]: " del_output
    if [ "$del_output" = "y" ]; then
      rm -rf $OUTPUT_DIR/*
      rm -rf $VERIFY_DIR/*
      echo "已删除所有输出文件"
    fi
    
    echo "清理完成!"
  else
    echo "操作已取消"
  fi
  
  read -p "按任意键继续..." key
}

# 执行完整迁移流程
run_full_process() {
  echo "开始执行完整迁移流程..."
  
  # 依次执行各步骤
  collect_db_info
  analyze_table_usage
  import_to_temp_table
  
  echo "前3个步骤已完成!"
  echo "请执行以下操作:"
  echo "1. 使用MySQL客户端连接到临时数据库并标记需要迁移的表"
  echo "2. 导出标记的表名列表到 $TABLES_LIST_FILE"
  
  read -p "以上操作完成后按Enter键继续..." key
  
  # 继续执行导出步骤
  export_tables
  
  echo "完整迁移流程已完成!下一步:"
  echo "1. 使用Navicat导入SQL文件到目标数据库"
  echo "2. 执行验证步骤检查数据一致性"
  
  read -p "按任意键继续..." key
}

# 主循环
while true; do
  show_menu
  read choice
  
  case $choice in
    1) collect_db_info ;;
    2) analyze_table_usage ;;
    3) import_to_temp_table ;;
    4) 
      echo "请使用MySQL客户端执行手动标记操作..."
      echo "1. 连接到临时数据库: mysql -h$SOURCE_HOST -P$SOURCE_PORT -u$SOURCE_USER -p$SOURCE_PASS $TEMP_DB"
      echo "2. 使用mark_tables_for_migration.sql中的SQL语句进行标记"
      echo "3. 导出标记的表名列表到 $TABLES_LIST_FILE"
      read -p "按任意键继续..." key
      ;;
    5) export_tables ;;
    6) verify_migration ;;
    7) cleanup ;;
    8) run_full_process ;;
    0) 
      echo "感谢使用MySQL增量迁移工具包!"
      exit 0
      ;;
    *) 
      echo "无效的选择，请输入0-8之间的数字"
      read -p "按任意键继续..." key
      ;;
  esac
done
