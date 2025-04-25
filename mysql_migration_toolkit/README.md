# MySQL数据库增量迁移工具包

本工具包用于在不停机的情况下，进行MySQL数据库的增量迁移。工具包通过分析数据库文件的修改时间和项目代码中的表使用情况，帮助确定哪些表需要增量迁移。

## 工具包结构

- `collect_db_files_info.sh` - 收集MySQL数据文件信息脚本
- `analyze_table_usage.py` - 分析表在Java项目中的使用情况
- `import_to_temp_table.sql` - 导入数据到临时表的SQL脚本
- `export_tables.sh` - 导出选定表的数据和结构
- `verify_migration.sh` - 验证迁移后的数据一致性
- `README.md` - 本说明文档

## 使用流程

1. 执行 `collect_db_files_info.sh` 收集数据库文件信息
2. 运行 `analyze_table_usage.py` 分析项目代码中的表使用情况
3. 使用 `import_to_temp_table.sql` 将数据导入MySQL临时表
4. 人工标记需要迁移的表
5. 执行 `export_tables.sh` 导出选定的表
6. 使用Navicat导入数据到目标服务器
7. 运行 `verify_migration.sh` 验证数据一致性

## 配置说明

各脚本中的配置参数需要根据实际环境进行调整，主要包括：

- 数据库连接信息（用户名、密码、主机等）
- MySQL数据目录路径
- 项目代码目录路径
- 导出文件存放路径

## 注意事项

- 脚本执行需要适当的系统和数据库权限
- 大表迁移可能需要较长时间，请合理安排维护窗口
- 在迁移过程中，建议暂停相关业务服务
- 保留原数据以便在必要时回滚
