#!/bin/bash

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}正在检查后端服务状态...${NC}"

# 检查后端服务是否运行
if curl -s http://localhost:8080/api/ai/list -o /dev/null; then
    echo -e "${GREEN}✓ 后端服务正在运行${NC}"
else
    echo -e "${RED}✗ 后端服务未运行或无法访问${NC}"
    echo -e "${YELLOW}可能的原因:${NC}"
    echo "  1. 后端服务未启动"
    echo "  2. 后端服务运行在不同的端口"
    echo "  3. 后端服务有错误"
    
    echo -e "\n${YELLOW}建议操作:${NC}"
    echo "  1. 在另一个终端窗口中运行后端服务:"
    echo "     cd backend && mvn spring-boot:run"
    echo "  2. 检查后端日志中的错误"
    echo "  3. 确认application.yml中的端口配置"
    
    # 检查Java进程
    echo -e "\n${YELLOW}检查Java进程:${NC}"
    javaProcesses=$(ps aux | grep java | grep -v grep)
    if [ -z "$javaProcesses" ]; then
        echo "  没有发现Java进程运行"
    else
        echo "  发现以下Java进程:"
        echo "$javaProcesses"
    fi
    
    # 检查8080端口
    echo -e "\n${YELLOW}检查8080端口:${NC}"
    portCheck=$(netstat -tuln 2>/dev/null | grep 8080 || echo "端口未被占用")
    echo "  $portCheck"
fi

echo -e "\n${YELLOW}检查前端代理配置:${NC}"
if [ -f "frontend/vue.config.js" ]; then
    echo -e "${GREEN}✓ 前端代理配置文件存在${NC}"
    echo "  配置内容:"
    grep -A 10 "proxy" frontend/vue.config.js
else
    echo -e "${RED}✗ 前端代理配置文件不存在${NC}"
fi

echo -e "\n${YELLOW}如需启动完整应用，请运行:${NC}"
echo "  ./start.sh" 