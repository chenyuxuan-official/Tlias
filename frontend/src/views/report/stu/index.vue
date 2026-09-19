<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
// ECharts 按需引入：只打包用到的图表类型和组件，比全量引入小很多
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import request from '@/utils/request'

echarts.use([
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  CanvasRenderer
])

const degreeChartRef = ref(null)
const clazzChartRef = ref(null)

// 图表实例存在组件外的变量里，卸载时才能拿到并销毁
let degreeChart = null
let clazzChart = null

// ===== 学员学历分布：后端返回 [{ name, value }, ...] =====
const loadDegreeData = async () => {
  try {
    const res = await request.get('/report/studentDegreeData')
    if (res.data.code === 1) {
      degreeChart = echarts.init(degreeChartRef.value)
      degreeChart.setOption({
        title: { text: '学员学历分布', left: 'center', top: 10 },
        tooltip: { trigger: 'item', formatter: '{b}：{c} 人（{d}%）' },
        legend: { bottom: 10 },
        series: [
          {
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '52%'],
            data: res.data.data,
            label: { formatter: '{b}\n{c} 人' },
            emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.3)' } }
          }
        ]
      })
    } else {
      ElMessage.error(res.data.msg || '学员学历数据加载失败')
    }
  } catch (err) {
    console.error('学员学历数据加载失败：', err)
    ElMessage.error('数据加载失败，请确认后端服务已启动')
  }
}

// ===== 各班级人数：后端返回 { clazzList: [...], dataList: [...] } =====
const loadClazzData = async () => {
  try {
    const res = await request.get('/report/studentCountData')
    if (res.data.code === 1) {
      const { clazzList, dataList } = res.data.data

      clazzChart = echarts.init(clazzChartRef.value)
      clazzChart.setOption({
        title: { text: '各班级人数', left: 'center', top: 10 },
        tooltip: { trigger: 'axis' },
        grid: { top: 70, left: 50, right: 30, bottom: 90 },
        xAxis: {
          type: 'category',
          data: clazzList,
          // 班级名称较长，标签倾斜避免重叠
          axisLabel: { rotate: 30, interval: 0 }
        },
        yAxis: { type: 'value', minInterval: 1 },
        series: [
          {
            type: 'bar',
            data: dataList,
            barWidth: '40%',
            itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] },
            label: { show: true, position: 'top' }
          }
        ]
      })
    } else {
      ElMessage.error(res.data.msg || '班级人数数据加载失败')
    }
  } catch (err) {
    console.error('班级人数数据加载失败：', err)
    ElMessage.error('数据加载失败，请确认后端服务已启动')
  }
}

// 窗口尺寸变化时让图表跟着自适应
const handleResize = () => {
  degreeChart?.resize()
  clazzChart?.resize()
}

onMounted(async () => {
  await loadDegreeData()
  await loadClazzData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例，避免内存泄漏
  degreeChart?.dispose()
  clazzChart?.dispose()
})
</script>

<template>
  <el-row :gutter="20">
    <el-col :span="12">
      <el-card>
        <div ref="degreeChartRef" class="chart"></div>
      </el-card>
    </el-col>

    <el-col :span="12">
      <el-card>
        <div ref="clazzChartRef" class="chart"></div>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
  /* 图表容器必须有明确高度，否则 ECharts 渲染不出来 */
  .chart {
    height: 420px;
  }
</style>
