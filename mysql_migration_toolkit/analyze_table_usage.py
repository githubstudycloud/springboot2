#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
MySQL表使用情况分析脚本
用于分析Java项目中对MySQL表的引用情况，包括：
- 表名在Java和XML文件中的出现次数
- SELECT/INSERT/UPDATE/DELETE操作的统计
- 引用文件的路径列表
"""

import os
import re
import csv
import json
import argparse
from collections import defaultdict

# 默认配置
DEFAULT_OUTPUT_DIR = "./output"
DEFAULT_OUTPUT_CSV = "table_usage_analysis.csv"
DEFAULT_OUTPUT_JSON = "table_usage_details.json"

# SQL操作模式
SQL_PATTERNS = {
    "select": r'(?i)(?:select|from|join)\s+(?:\w+\.)?(%s)\b',
    "insert": r'(?i)insert\s+into\s+(?:\w+\.)?(%s)\b',
    "update": r'(?i)update\s+(?:\w+\.)?(%s)\b',
    "delete": r'(?i)delete\s+from\s+(?:\w+\.)?(%s)\b'
}

def parse_args():
    """解析命令行参数"""
    parser = argparse.ArgumentParser(description='分析MySQL表在Java项目中的使用情况')
    parser.add_argument('-i', '--input', required=True, 
                        help='包含表名列表的CSV文件路径')
    parser.add_argument('-p', '--projects', required=True, nargs='+',
                        help='要分析的Java项目目录，可指定多个')
    parser.add_argument('-o', '--output-dir', default=DEFAULT_OUTPUT_DIR,
                        help=f'输出目录，默认为"{DEFAULT_OUTPUT_DIR}"')
    parser.add_argument('-c', '--csv', default=DEFAULT_OUTPUT_CSV,
                        help=f'输出CSV文件名，默认为"{DEFAULT_OUTPUT_CSV}"')
    parser.add_argument('-j', '--json', default=DEFAULT_OUTPUT_JSON,
                        help=f'输出JSON文件名，默认为"{DEFAULT_OUTPUT_JSON}"')
    return parser.parse_args()

def read_table_names(csv_file):
    """从CSV文件读取表名列表"""
    table_names = []
    with open(csv_file, 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        next(reader)  # 跳过表头
        for row in reader:
            if len(row) >= 3:  # 确保行有足够的列
                table_name = row[2].strip()  # 第3列是表名
                if table_name:
                    table_names.append(table_name)
    return table_names

def find_files(project_dirs, extensions):
    """递归查找指定后缀的文件"""
    files = []
    for project_dir in project_dirs:
        for root, _, filenames in os.walk(project_dir):
            for filename in filenames:
                if any(filename.endswith(ext) for ext in extensions):
                    files.append(os.path.join(root, filename))
    return files

def analyze_file_content(file_path, table_names, table_usage):
    """分析文件内容中的表名引用"""
    file_ext = os.path.splitext(file_path)[1].lower()
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except UnicodeDecodeError:
        try:
            # 尝试其他编码
            with open(file_path, 'r', encoding='latin1') as f:
                content = f.read()
        except Exception as e:
            print(f"警告: 无法读取文件 {file_path}: {e}")
            return
    
    for table_name in table_names:
        # 检查表名是否在文件中出现
        if re.search(r'\b' + re.escape(table_name) + r'\b', content, re.IGNORECASE):
            # 更新表使用情况
            is_java = file_ext == '.java'
            is_xml = file_ext in ('.xml', '.hbm.xml', '.mapper')
            
            if is_java:
                table_usage[table_name]['java_references'] += 1
                table_usage[table_name]['java_files'].append(file_path)
            elif is_xml:
                table_usage[table_name]['xml_references'] += 1
                table_usage[table_name]['xml_files'].append(file_path)
            
            # 分析SQL操作类型
            if is_java or is_xml:
                for op_type, pattern in SQL_PATTERNS.items():
                    pattern = pattern % re.escape(table_name)
                    matches = re.findall(pattern, content)
                    if matches:
                        table_usage[table_name][f'{op_type}_count'] += len(matches)
                        # 记录引用上下文（仅记录前10个）
                        if len(table_usage[table_name][f'{op_type}_contexts']) < 10:
                            for match in matches:
                                # 找到匹配周围的上下文
                                start = max(0, content.find(match) - 50)
                                end = min(len(content), content.find(match) + len(match) + 50)
                                context = content[start:end].replace('\n', ' ').strip()
                                table_usage[table_name][f'{op_type}_contexts'].append(
                                    {'file': file_path, 'context': context}
                                )

def main():
    """主函数"""
    args = parse_args()
    
    # 创建输出目录
    os.makedirs(args.output_dir, exist_ok=True)
    
    # 读取表名列表
    print(f"读取表名列表从: {args.input}")
    table_names = read_table_names(args.input)
    print(f"共读取到 {len(table_names)} 个表名")
    
    # 初始化表使用情况字典
    table_usage = defaultdict(lambda: {
        'java_references': 0,
        'xml_references': 0,
        'select_count': 0,
        'insert_count': 0,
        'update_count': 0,
        'delete_count': 0,
        'java_files': [],
        'xml_files': [],
        'select_contexts': [],
        'insert_contexts': [],
        'update_contexts': [],
        'delete_contexts': []
    })
    
    # 查找所有Java和XML文件
    print(f"正在查找Java和XML文件...")
    files = find_files(args.projects, ['.java', '.xml', '.hbm.xml', '.mapper'])
    print(f"共找到 {len(files)} 个文件")
    
    # 分析文件内容
    print(f"正在分析文件内容...")
    for i, file_path in enumerate(files):
        if i % 100 == 0:
            print(f"已处理 {i}/{len(files)} 个文件...")
        analyze_file_content(file_path, table_names, table_usage)
    
    # 生成CSV报告
    csv_path = os.path.join(args.output_dir, args.csv)
    with open(csv_path, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([
            '表名', 'Java文件引用次数', 'XML文件引用次数', 
            'SELECT引用次数', 'INSERT引用次数', 'UPDATE引用次数', 'DELETE引用次数',
            '引用文件列表'
        ])
        
        for table_name in table_names:
            usage = table_usage[table_name]
            # 合并文件列表
            file_list = list(set(usage['java_files'] + usage['xml_files']))
            # 只取前5个文件，避免CSV单元格过大
            if len(file_list) > 5:
                file_list = file_list[:5] + [f"...还有{len(file_list)-5}个文件"]
            
            writer.writerow([
                table_name,
                usage['java_references'],
                usage['xml_references'],
                usage['select_count'],
                usage['insert_count'],
                usage['update_count'],
                usage['delete_count'],
                '; '.join(file_list)
            ])
    
    # 生成JSON详细报告
    json_path = os.path.join(args.output_dir, args.json)
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(table_usage, f, ensure_ascii=False, indent=2)
    
    print(f"分析完成！")
    print(f"CSV报告已保存到: {csv_path}")
    print(f"JSON详细报告已保存到: {json_path}")
    print(f"下一步: 将结果导入到临时表中进行人工筛选")

if __name__ == '__main__':
    main()
