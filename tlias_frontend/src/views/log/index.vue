<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

// 查询操作日志
const search = async () => {
  try {
    const res = await request.get('/logs', {
      params: {
        page: page.value,
        pageSize: pageSize.value
      }
    })

    if (res.data.code === 1) {
      tableData.value = res.data.data.rows || []
      total.value = res.data.data.total || 0
    } else {
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    console.error('查询操作日志失败：', err)
    ElMessage.error('查询失败，请确认后端服务已启动（http://localhost:8080）')
  }
}

const handlePageChange = () => search()

const handleSizeChange = () => {
  page.value = 1
  search()
}

// 类名是完整包路径（com.shanfu.app.controller.EmpController），表格里只显示最后一段
const shortClassName = (className) => {
  if (!className) return ''
  const parts = className.split('.')
  return parts[parts.length - 1]
}

// 时间格式化：2026-08-09T18:46:42 → 2026-08-09 18:46:42
const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ')
}

onMounted(() => {
  search()
})
</script>

<template>
  <el-card>
    <!-- 操作日志列表 -->
    <el-table :data="tableData" border style="width: 100%">
      <!-- 展开行：显示完整的方法参数、返回值、类名（表格里放不下） -->
      <el-table-column type="expand">
        <template #default="scope">
          <div class="log-detail">
            <p><span class="label">完整类名：</span>{{ scope.row.className || '无' }}</p>
            <p><span class="label">方法参数：</span>{{ scope.row.methodParams || '无' }}</p>
            <p><span class="label">返回值：</span>{{ scope.row.returnValue || '无' }}</p>
          </div>
        </template>
      </el-table-column>

      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="operateEmpName" label="操作人" width="110" align="center" />

      <el-table-column label="操作时间" width="180" align="center">
        <template #default="scope">{{ formatTime(scope.row.operateTime) }}</template>
      </el-table-column>

      <el-table-column label="操作类" align="center">
        <template #default="scope">
          <el-tooltip :content="scope.row.className" placement="top">
            <span>{{ shortClassName(scope.row.className) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column prop="methodName" label="操作方法" width="130" align="center" />

      <el-table-column label="耗时" width="120" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.costTime > 100 ? 'warning' : 'success'" size="small">
            {{ scope.row.costTime }} ms
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      class="pagination"
      :page-sizes="[5, 10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </el-card>
</template>

<style scoped>
  .pagination {
    margin-top: 16px;
    justify-content: flex-end;
  }

  .log-detail {
    padding: 10px 30px;
    font-size: 13px;
    color: #606266;
    line-height: 1.8;
    /* 参数和返回值可能很长，允许换行 */
    word-break: break-all;
  }

  .log-detail .label {
    color: #909399;
    font-weight: bold;
  }
</style>
