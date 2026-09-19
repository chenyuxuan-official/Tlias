<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

// 四个模块的总数
const counts = ref({
  emp: 0,
  stu: 0,
  clazz: 0,
  dept: 0
})

// 加载各模块总数
// 分页接口的返回结构是 { total, rows }，把 pageSize 设成 1 只取 total，避免拉一堆数据
const loadCounts = async () => {
  try {
    const [empRes, stuRes, clazzRes, deptRes] = await Promise.all([
      request.get('/emps', { params: { page: 1, pageSize: 1 } }),
      request.get('/students', { params: { page: 1, pageSize: 1 } }),
      request.get('/clazzs', { params: { page: 1, pageSize: 1 } }),
      request.get('/depts')
    ])

    counts.value = {
      emp: empRes.data.data?.total || 0,
      stu: stuRes.data.data?.total || 0,
      clazz: clazzRes.data.data?.total || 0,
      // 部门接口返回的是完整数组，不是分页对象，所以取 length
      dept: deptRes.data.data?.length || 0
    }
  } catch (err) {
    console.error('首页数据加载失败：', err)
    ElMessage.error('数据加载失败，请确认后端服务已启动（http://localhost:8080）')
  }
}

// 卡片展示用的配置
const countItems = computed(() => [
  { key: 'emp', label: '员工总数', value: counts.value.emp, color: '#409eff' },
  { key: 'stu', label: '学员总数', value: counts.value.stu, color: '#67c23a' },
  { key: 'clazz', label: '班级总数', value: counts.value.clazz, color: '#e6a23c' },
  { key: 'dept', label: '部门总数', value: counts.value.dept, color: '#9b59b6' }
])

// 今天的日期，用中文格式显示
const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
})

onMounted(() => {
  loadCounts()
})
</script>

<template>
  <div class="home">
    <!-- ===== 欢迎区 ===== -->
    <el-card class="welcome-card">
      <h2 class="welcome-title">欢迎使用 tlias 智能学习辅助系统</h2>
      <p class="welcome-sub">{{ today }}，祝您工作顺利</p>
      <p class="welcome-tip">通过左侧菜单可以进入部门、员工、班级、学员管理以及数据统计模块</p>
    </el-card>

    <!-- ===== 数据概览 ===== -->
    <el-row :gutter="20">
      <el-col v-for="item in countItems" :key="item.key" :span="6">
        <el-card class="count-card">
          <div class="count-value" :style="{ color: item.color }">{{ item.value }}</div>
          <div class="count-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
  .welcome-card {
    margin-bottom: 20px;
  }

  .welcome-title {
    font-size: 22px;
    color: #303133;
    margin-bottom: 10px;
  }

  .welcome-sub {
    font-size: 14px;
    color: #606266;
    margin-bottom: 6px;
  }

  .welcome-tip {
    font-size: 13px;
    color: #909399;
  }

  .count-card {
    text-align: center;
    padding: 10px 0;
  }

  .count-value {
    font-size: 34px;
    font-weight: bold;
    line-height: 1.2;
  }

  .count-label {
    font-size: 14px;
    color: #909399;
    margin-top: 10px;
  }
</style>
