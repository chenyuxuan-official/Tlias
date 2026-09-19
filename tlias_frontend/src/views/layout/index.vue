<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppAside from '@/components/AppAside.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<template>
  <!-- 布局组件：所有后台页面共用的"外壳"（顶栏 + 左侧菜单 + 页脚） -->
  <el-container class="layout">
    <!-- 1. 顶栏 -->
    <el-header class="layout-header">
      <AppHeader />
    </el-header>

    <el-container class="layout-body">
      <!-- 2. 左侧菜单栏 -->
      <el-aside width="200px" class="layout-aside">
        <AppAside />
      </el-aside>

      <!-- 3. 右侧核心展示区域：子路由渲染在这里 -->
      <el-main class="layout-main">
        <router-view />
        <AppFooter />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
  /* 整个后台占满一屏 */
  .layout {
    height: 100vh;
  }

  /* el-header 默认高 60px 且带内边距，这里交给 AppHeader 自己控制 */
  .layout-header {
    height: 50px;
    padding: 0;
  }

  /* 下方区域占满剩余高度 */
  .layout-body {
    flex: 1;
    /* 高度由内容区自己滚动，避免撑破一屏 */
    overflow: hidden;
  }

  /* 左侧菜单栏 */
  .layout-aside {
    background-color: #ffffff;
    border-right: 1px solid #e4e7ed;
    overflow-y: auto;
  }

  /* 右侧核心展示区：数据多时在这里滚动 */
  .layout-main {
    background-color: #f5f5f5;
    padding: 20px;
    overflow-y: auto;
  }
</style>
