<script setup>
import { useRoute } from 'vue-router'

// useRoute()：拿到当前"正在访问"的路由信息
// 注意与 useRouter() 区分：useRoute 是读信息，useRouter 是做跳转
const route = useRoute()
</script>

<template>
  <el-card class="placeholder">
    <el-empty>
      <!-- 用插槽自定义说明文案，内容取自路由表里的 meta -->
      <template #description>
        <p class="title">{{ route.meta.title }}</p>
        <p class="desc">{{ route.meta.desc || '该功能页面待开发' }}</p>
      </template>
    </el-empty>
  </el-card>
</template>

<style scoped>
  .placeholder {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 360px;
  }

  .title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
  }

  .desc {
    font-size: 14px;
    color: #909399;
  }
</style>
