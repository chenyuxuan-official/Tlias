import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 按需自动引入 Element Plus 所需的两个插件
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 自动导入 Element Plus 的"函数式 API"，如 ElMessage、ElMessageBox、ElNotification
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    // 自动按需注册 Element Plus 的"模板组件"（<el-button> 等）及其样式
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 开发服务器配置
  server: {
    // 用代理解决前后端分离的跨域问题：浏览器只访问 5173，由 Vite 转发给 8080
    proxy: {
      // 前端请求 /api/depts → 实际转发到 http://localhost:8080/depts
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 转发时把 /api 前缀去掉，后端接口本身没有这个前缀
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
