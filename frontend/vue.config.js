// 移除对defineConfig的引用
// const { defineConfig } = require('@vue/cli-service')

module.exports = {
  transpileDependencies: [],
  devServer: {
    port: 8081, // 前端服务端口，避免与后端的8080端口冲突
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        logLevel: 'debug', // 添加调试日志
        onError: (err, req, res) => {
          console.error('Proxy error:', err);
          res.writeHead(500, {
            'Content-Type': 'text/plain',
          });
          res.end('后端服务连接失败，请确保后端服务正在运行。');
        },
        onProxyReq: (proxyReq, req, res) => {
          console.log(`Proxying request to: ${req.url}`);
        },
        onProxyRes: (proxyRes, req, res) => {
          console.log(`Received response from: ${req.url}, status: ${proxyRes.statusCode}`);
        }
      }
    }
  },
  // 生产环境配置
  productionSourceMap: false,
  // 输出目录
  outputDir: '../backend/src/main/resources/static',
  // 静态资源目录
  assetsDir: 'static'
} 