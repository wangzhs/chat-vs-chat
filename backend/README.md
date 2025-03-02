# Chat-vs-Chat 后端

这是Chat-vs-Chat系统的后端应用，使用Spring Boot构建。

## 功能特点

- 使用YAML进行动态AI提供商配置
- 支持多个AI提供商（包括国内外主流大语言模型）
- 并发消息处理
- 聊天和AI管理的RESTful API

## 前提条件

- Java 11或更高版本
- Maven 3.6或更高版本

## 运行应用

1. 设置API密钥作为环境变量：
   ```
   # 国外AI服务
   export OPENAI_API_KEY=your-openai-api-key
   export ANTHROPIC_API_KEY=your-anthropic-api-key
   export GOOGLE_API_KEY=your-google-api-key
   
   # 国内AI服务
   export BAIDU_API_KEY=your-baidu-api-key
   export ALIYUN_API_KEY=your-aliyun-api-key
   export XUNFEI_API_KEY=your-xunfei-api-key
   export ZHIPU_API_KEY=your-zhipu-api-key
   export DEEPSEEK_API_KEY=your-deepseek-api-key
   ```

2. 构建应用：
   ```
   mvn clean package
   ```

3. 运行应用：
   ```
   java -jar target/chat-vs-chat-0.0.1-SNAPSHOT.jar
   ```

## API端点

### AI管理

- `GET /api/ai/list` - 获取所有可用的AI
- `GET /api/ai/{name}` - 通过名称获取特定AI
- `POST /api/ai` - 添加新的AI
- `PUT /api/ai/{name}` - 更新现有AI
- `DELETE /api/ai/{name}` - 删除AI
- `POST /api/ai/reload` - 从YAML重新加载AI配置

### 聊天

- `POST /api/chat/send` - 向一个或所有AI发送消息

## 配置

AI提供商在`application.yml`中配置。每个提供商具有以下属性：

- `id` - 唯一标识符
- `name` - 显示名称
- `description` - 简短描述
- `apiEndpoint` - API端点URL
- `apiKey` - API密钥（最好通过环境变量设置）
- `headers` - 额外的HTTP头
- `parameters` - 额外的请求参数
- `enabled` - 提供商是否启用（默认为false，需要手动启用）

示例：
```yaml
ai:
  providers:
    - id: gpt-3.5
      name: GPT-3.5
      description: OpenAI的GPT-3.5 Turbo模型
      apiEndpoint: https://api.openai.com/v1/chat/completions
      apiKey: ${OPENAI_API_KEY}
      parameters:
        model: gpt-3.5-turbo
        temperature: 0.7
      enabled: false
    
    - id: wenxin
      name: 文心一言
      description: 百度文心一言大语言模型
      apiEndpoint: https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions
      apiKey: ${BAIDU_API_KEY}
      parameters:
        model: ERNIE-Bot-4
        temperature: 0.7
      enabled: false
```

## WebClient配置

您可以在`application.yml`中配置WebClient的行为：

```yaml
webclient:
  timeout: 60000  # 连接超时时间（毫秒）
  max-in-memory-size: 16777216  # 最大内存大小（16MB）
  proxy:
    enabled: false  # 是否启用代理
    host: 127.0.0.1  # 代理主机
    port: 7890  # 代理端口
```

## 国内AI服务说明

本应用已集成以下国内主流大语言模型：

1. **百度文心一言** - 百度开发的大语言模型，支持多种自然语言处理任务
2. **阿里通义千问** - 阿里云开发的大语言模型，具有强大的知识理解和推理能力
3. **讯飞星火** - 科大讯飞开发的认知大模型，在中文理解方面表现优异
4. **智谱ChatGLM** - 清华大学与智谱AI联合开发的开源双语对话语言模型
5. **DeepSeek** - 深度求索开发的大语言模型，在代码和中文理解方面表现出色

## 启用AI服务

所有AI服务默认处于关闭状态。您可以通过以下方式启用所需的AI服务：

1. 在`application.yml`文件中将相应AI的`enabled`属性设置为`true`
2. 使用API端点动态启用：
   ```
   PUT /api/ai/{name}
   {
     "enabled": true
   }
   ```

## 常见错误处理

### 连接超时错误

如果遇到类似以下错误：
```
Error communicating with AI: GPT-3.5, Error: connection timed out: api.openai.com/199.16.156.7:443
```

这表示无法连接到AI服务提供商的API服务器。常见原因包括：

1. **网络连接问题**：
   - 检查您的网络连接是否正常
   - 某些API服务器（如OpenAI）在某些地区可能无法直接访问

2. **防火墙或网络限制**：
   - 公司或组织网络可能阻止了对API服务器的访问
   - 检查防火墙设置

3. **DNS解析问题**：
   - 尝试使用IP地址而不是域名
   - 或者修改本地hosts文件

解决方法：

1. **配置代理**：
   ```yaml
   webclient:
     proxy:
       enabled: true
       host: 127.0.0.1  # 代理服务器地址
       port: 7890       # 代理服务器端口
   ```

2. **增加超时时间**：
   ```yaml
   webclient:
     timeout: 60000  # 60秒
   ```

3. **使用国内可访问的AI服务**：
   - 如果无法访问国外服务，可以启用国内AI服务如文心一言、通义千问等

### 400 Bad Request

如果遇到类似以下错误：
```
400 BAD_REQUEST from POST https://api.deepseek.com/v1/chat/completions [DefaultWebClient]
```

这表示请求格式不正确。常见原因包括：

1. **请求体格式错误**：每个AI服务要求特定的请求格式
   - DeepSeek和OpenAI需要`messages`数组，每条消息包含`role`和`content`
   - 确保没有多余或错误的字段

2. **参数类型错误**：
   - 数值参数（如temperature）应该是数字而不是字符串
   - 确保布尔值正确表示（true/false，不带引号）

3. **API密钥格式错误**：
   - 确保Authorization头使用正确的格式（通常是`Bearer YOUR_API_KEY`）
   - 检查API密钥是否包含多余的空格或特殊字符

### 422 Unprocessable Entity

如果遇到类似以下错误：
```
WebClientResponseException$UnprocessableEntity: 422 Unprocessable Entity from POST https://api.deepseek.com/v1/chat/completions
```

这通常表示请求格式正确，但内容有语义错误：

1. **参数值超出范围**：
   - temperature通常应在0-1之间
   - max_tokens不能超过模型限制

2. **必需字段缺失**：
   - 确保包含所有必需字段（如model、messages等）

3. **模型名称错误**：
   - 确保使用正确的模型名称（如`deepseek-chat`而不是`deepseek`）

### 401 Unauthorized

如果遇到401错误，通常是API密钥问题：
- 确保API密钥正确设置
- 检查API密钥是否过期
- 确认API密钥有足够的权限和配额

### 429 Too Many Requests

如果遇到429错误，表示超出了API调用限制：
- 减少API调用频率
- 考虑升级API使用计划
- 实现请求限流和重试机制

## 调试技巧

1. **启用详细日志**：
   ```yaml
   logging:
     level:
       com.example.chatvschat: DEBUG
       org.springframework.web.reactive.function.client: DEBUG
   ```

2. **检查请求体**：
   - 在日志中查看完整的请求体JSON
   - 确保JSON格式正确，没有多余的逗号或引号

3. **直接使用curl测试**：
   ```bash
   curl -X POST https://api.deepseek.com/v1/chat/completions \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_API_KEY" \
     -d '{
       "model": "deepseek-chat",
       "messages": [{"role": "user", "content": "Hello"}],
       "temperature": 0.7,
       "max_tokens": 1000
     }'
   ```

使用这些服务前，请确保已在相应平台注册并获取API密钥。 