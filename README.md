# Chat-vs-Chat

Chat-vs-Chat是一个比较不同AI聊天模型的应用程序，允许用户同时向多个AI发送消息并比较它们的回复。

## 功能特点

- 支持多种AI服务提供商，包括OpenAI、Anthropic Claude、Google Gemini以及国内的文心一言、通义千问等
- 使用LangChain4j框架统一处理不同AI服务的请求
- 同时向多个AI发送消息并比较它们的回复
- 可以单独启用或禁用特定的AI服务
- 支持代理配置，方便在不同网络环境下使用

## 技术栈

- 后端：Spring Boot、LangChain4j
- 前端：Vue.js、Element UI
- 构建工具：Maven、npm

## 快速开始

### 前提条件

- Java 11或更高版本
- Maven 3.6或更高版本
- Node.js 14或更高版本
- npm 6或更高版本

### 启动应用程序

使用提供的启动脚本同时启动前端和后端服务：

```bash
chmod +x start.sh
./start.sh
```

或者，您可以分别启动后端和前端服务：

1. 启动后端服务：

```bash
cd backend
mvn spring-boot:run
```

2. 启动前端服务：

```bash
cd frontend
npm install
npm run serve
```

应用程序将在以下地址运行：
- 后端：http://localhost:8080
- 前端：http://localhost:8081



## LangChain4j集成

本项目使用LangChain4j框架来统一处理不同AI服务的请求。LangChain4j是LangChain的Java版本，提供了一套统一的API来与各种大语言模型进行交互。

### 支持的AI模型

通过LangChain4j，我们支持以下AI模型：

- OpenAI (GPT-3.5, GPT-4)
- Anthropic Claude
- Google Gemini
- 百度文心一言
- 阿里云通义千问
- 科大讯飞星火
- 智谱AI ChatGLM
- DeepSeek

### 自定义AI配置

您可以在`application.yml`文件中自定义每个AI模型的配置，包括：

- 模型名称
- 温度参数
- 最大令牌数
- 系统消息
- 是否启用

## 常见问题

### API密钥错误

如果您遇到API密钥相关的错误，请确保：
1. 您已经设置了正确的API密钥（通过环境变量或配置文件）
2. API密钥有效且未过期
3. 您的账户有足够的配额

## 贡献

欢迎贡献代码、报告问题或提出改进建议。请通过GitHub Issues或Pull Requests参与项目开发。

## 待办

- [ ] 聊天页面支持艾特多个ai
- [ ] 成语接龙
- [ ] 圆桌会议
- [ ] AI辩论

## 许可证

本项目采用MIT许可证。详情请参阅LICENSE文件。 