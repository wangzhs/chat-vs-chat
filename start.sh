#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Chat-vs-Chat application...${NC}"

# 检查后端目录是否存在
if [ ! -d "backend" ]; then
  echo -e "${RED}Error: backend directory not found!${NC}"
  exit 1
fi

# 检查前端目录是否存在
if [ ! -d "frontend" ]; then
  echo -e "${RED}Error: frontend directory not found!${NC}"
  exit 1
fi

# 启动后端服务
echo -e "${YELLOW}Starting backend service...${NC}"
cd backend
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

echo -e "${GREEN}Backend service started with PID: ${BACKEND_PID}${NC}"
echo -e "${YELLOW}Waiting for backend to initialize (10 seconds)...${NC}"
sleep 10

# 检查后端是否成功启动
if ! ps -p $BACKEND_PID > /dev/null; then
  echo -e "${RED}Error: Backend failed to start. Check backend.log for details.${NC}"
  exit 1
fi

# 启动前端服务
echo -e "${YELLOW}Starting frontend service...${NC}"
cd frontend
npm run serve > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo -e "${GREEN}Frontend service started with PID: ${FRONTEND_PID}${NC}"
echo -e "${YELLOW}Waiting for frontend to initialize (5 seconds)...${NC}"
sleep 5

# 检查前端是否成功启动
if ! ps -p $FRONTEND_PID > /dev/null; then
  echo -e "${RED}Error: Frontend failed to start. Check frontend.log for details.${NC}"
  kill $BACKEND_PID
  exit 1
fi

echo -e "${GREEN}Chat-vs-Chat application started successfully!${NC}"
echo -e "${GREEN}Backend running on http://localhost:8080${NC}"
echo -e "${GREEN}Frontend running on http://localhost:8081${NC}"
echo -e "${YELLOW}Logs are being written to backend.log and frontend.log${NC}"
echo -e "${YELLOW}Press Ctrl+C to stop the application${NC}"

# 等待用户按下Ctrl+C
trap "echo -e '${YELLOW}Stopping Chat-vs-Chat application...${NC}'; kill $BACKEND_PID $FRONTEND_PID; echo -e '${GREEN}Application stopped.${NC}'" INT
wait 