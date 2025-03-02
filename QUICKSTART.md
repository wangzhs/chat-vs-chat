# Chat-vs-Chat 快速启动指南

本指南将帮助您快速设置和运行Chat-vs-Chat应用。

## 前提条件

确保您的系统已安装以下软件：

- Java 11或更高版本
- Node.js 16或更高版本
- npm 8或更高版本
- Maven 3.6或更高版本

## 快速启动（使用脚本）

我们提供了一个便捷的启动脚本，可以同时启动前端和后端服务：

```bash
# 添加执行权限
chmod +x start.sh

# 运行启动脚本
./start.sh
```

启动后，您可以访问：
- 前端界面：http://localhost:8081
- 后端API：http://localhost:8080

## 手动启动

如果您想分别启动前端和后端，请按照以下步骤操作：

### 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 运行。

### 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端服务将在 http://localhost:8081 运行。

## 配置AI服务

默认情况下，所有AI服务都是禁用的。您需要在使用前启用所需的服务：

1. 编辑 `backend/src/main/resources/application.yml` 文件
2. 找到您想使用的AI服务，将其 `enabled` 属性设置为 `true`
3. 设置相应的API密钥（最好通过环境变量）

例如，要启用GPT-3.5和DeepSeek：

```yaml
- id: gpt-3.5
  # ... 其他配置 ...
  enabled: true
  
- id: deepseek
  # ... 其他配置 ...
  enabled: true
```

## 使用应用

1. 打开浏览器访问 http://localhost:8081
2. 在聊天界面中，您可以：
   - 发送消息给所有已启用的AI
   - 使用 `@AI名称: 消息内容` 格式向特定AI发送消息
   - 点击侧边栏中的AI名称，自动在输入框中插入@mention

## 故障排除

- 如果前端无法连接到后端，请检查 `vue.config.js` 中的代理配置
- 如果AI服务返回错误，请检查您的API密钥是否正确
- 查看后端日志以获取更详细的错误信息 