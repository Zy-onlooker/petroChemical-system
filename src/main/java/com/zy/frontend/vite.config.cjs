import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    cors: true, // 前端服务允许跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false, // 忽略HTTPS（开发环境）
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/ws/, '/ws'), // 确保路径正确转发
        // 关键：配置代理超时，避免连接被提前中止
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('WebSocket代理错误:', err);
          });
          proxy.on('close', (err, req, res) => {
            console.log('WebSocket代理连接关闭');
          });
        }
      }
    }
  }
})