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

const jobChartRef = ref(null)
const genderChartRef = ref(null)

// 图表实例存在组件外的变量里，卸载时才能拿到并销毁
let jobChart = null
let genderChart = null

// ===== 员工职位分布：后端返回 { jobList: [...], dataList: [...] } =====
const loadJobData = async () => {
  try {
    const res = await request.get('/report/empJobData')
    if (res.data.code === 1) {
      const { jobList, dataList } = res.data.data

      jobChart = echarts.init(jobChartRef.value)
      jobChart.setOption({
        title: { text: '员工职位分布', left: 'center', top: 10 },
        tooltip: { trigger: 'axis' },
        // 手动留出标题和轴标签的空间
        grid: { top: 70, left: 50, right: 30, bottom: 50 },
        xAxis: { type: 'category', data: jobList },
        // minInterval: 1 避免人数少时纵轴出现 0.5 这种小数刻度
        yAxis: { type: 'value', minInterval: 1 },
        series: [
          {
            type: 'bar',
            data: dataList,
            barWidth: '40%',
            itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] },
            label: { show: true, position: 'top' }
          }
        ]
      })
    } else {
      ElMessage.error(res.data.msg || '员工职位数据加载失败')
    }
  } catch (err) {
    console.error('员工职位数据加载失败：', err)
    ElMessage.error('数据加载失败，请确认后端服务已启动')
  }
}

// ===== 员工性别分布：后端返回 [{ name, value }, ...] =====
const loadGenderData = async () => {
  try {
    const res = await request.get('/report/empGenderData')
    if (res.data.code === 1) {
      genderChart = echarts.init(genderChartRef.value)
      genderChart.setOption({
        title: { text: '员工性别分布', left: 'center', top: 10 },
        tooltip: { trigger: 'item', formatter: '{b}：{c} 人（{d}%）' },
        legend: { bottom: 10 },
        series: [
          {
            type: 'pie',
            // 做成环形图，中间留白更清爽
            radius: ['40%', '65%'],
            center: ['50%', '52%'],
            data: res.data.data,
            label: { formatter: '{b}\n{c} 人' },
            emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.3)' } }
          }
        ]
      })
    } else {
      ElMessage.error(res.data.msg || '员工性别数据加载失败')
    }
  } catch (err) {
    console.error('员工性别数据加载失败：', err)
    ElMessage.error('数据加载失败，请确认后端服务已启动')
  }
}

// 窗口尺寸变化时让图表跟着自适应
const handleResize = () => {
  jobChart?.resize()
  genderChart?.resize()
}

onMounted(async () => {
  await loadJobData()
  await loadGenderData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例，避免内存泄漏
  jobChart?.dispose()
  genderChart?.dispose()
})
</script>

<template>
  <el-row :gutter="20">
    <el-col :span="12">
      <el-card>
        <div ref="jobChartRef" class="chart"></div>
      </el-card>
    </el-col>

    <el-col :span="12">
      <el-card>
        <div ref="genderChartRef" class="chart"></div>
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
