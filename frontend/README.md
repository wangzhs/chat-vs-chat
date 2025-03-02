# Chat-vs-Chat 前端

这是Chat-vs-Chat系统的前端应用，使用Vue 3构建。

## 功能特点

- 群组聊天界面，支持与多个AI助手同时对话
- 支持通过@mentions向特定AI发送消息
- 实时消息显示
- 可用AI列表，支持快速选择

## 项目设置与启动

### 前提条件

- Node.js 16+
- npm 8+

### 安装依赖

```bash
# 进入前端项目目录
cd frontend

# 安装依赖
npm install
```

### 开发环境启动

```bash
# 启动开发服务器（热重载）
npm run serve
```

应用将在 http://localhost:8081 运行（如果该端口可用）。

### 生产环境构建

```bash
# 构建生产版本
npm run build
```

构建后的文件将生成在 `dist/` 目录中。

### 代码检查

```bash
# 运行代码检查
npm run lint
```

## 项目结构

```
frontend/
├── public/              # 静态资源
├── src/
│   ├── assets/          # 图片等资源文件
│   ├── components/      # Vue组件
│   │   ├── ChatComponent.vue    # 聊天组件
│   │   └── AIListComponent.vue  # AI列表组件
│   ├── views/           # 页面视图
│   │   └── ChatView.vue          # 主聊天视图
│   ├── router/          # 路由配置
│   ├── store/           # Vuex状态管理
│   ├── App.vue          # 根组件
│   └── main.js          # 应用入口
├── .eslintrc.js         # ESLint配置
├── babel.config.js      # Babel配置
├── package.json         # 项目依赖
└── vue.config.js        # Vue CLI配置
```

## 与后端连接

前端应用默认会将API请求发送到同一域名下的后端服务。如果您的后端运行在不同的地址，需要配置代理或修改API请求地址。

### 配置代理（开发环境）

在项目根目录创建或编辑 `vue.config.js` 文件：

```javascript
module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端服务地址
        changeOrigin: true
      }
    }
  }
}
```

## 使用说明

- 在聊天输入框中输入消息并按Enter或点击发送按钮，将消息发送给所有AI
- 要向特定AI发送消息，使用格式：`@AI名称: 您的消息`
- 点击侧边栏中的AI，自动在输入框中插入@mention格式

## 常见问题解决

### ESLint配置错误

如果遇到以下错误：
```
ERROR in [eslint] No ESLint configuration found in /path/to/frontend/src.
```

解决方法：
1. 确保项目根目录中存在 `.eslintrc.js` 文件
2. 确保已安装所有必要的ESLint依赖：
   ```bash
   npm install --save-dev eslint eslint-plugin-vue @babel/eslint-parser
   ```
3. 重新启动开发服务器：
   ```bash
   npm run serve
   ```

## 自定义配置

查看 [Vue CLI配置参考](https://cli.vuejs.org/config/)。

## 依赖项

主要依赖：
- Vue 3
- Vuex 4
- Vue Router 4
- Axios

## 浏览器兼容性

支持所有现代浏览器和IE 11+（需要相应的polyfills）。 