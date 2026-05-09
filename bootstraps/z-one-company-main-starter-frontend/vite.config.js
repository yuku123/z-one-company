import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  // Docker 部署模式: 使用 /api 相对路径（通过 Nginx 代理）
  // 本地开发模式: 使用 localhost:8888
  const isDev = mode === 'development'

  return {
    plugins: [react()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
    server: {
      port: 3000,
      host: '0.0.0.0',
      proxy: isDev ? {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/auth': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/account': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/permission': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/tenant': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/domain': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/org': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/dept': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/group': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/audit': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
      } : undefined,
    },
  }
})