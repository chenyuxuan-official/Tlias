<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

// request 实例的 baseURL 已经是 /api（见 src/utils/request.js）
// 这里写 /depts，实际请求 http://localhost:8080/depts
const API = '/depts'

// ===== 列表相关 =====
const rawList = ref([])      // 后端返回的原始数据
const deptList = ref([])     // 表格实际展示的数据（按名称过滤后的）
const searchName = ref('')   // 搜索框绑定的部门名称

// ===== 弹窗表单相关 =====
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const formRef = ref()        // 表单实例，用来调用 validate()
const form = reactive({
  id: null,
  name: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 10, message: '部门名称长度需在 2 到 10 个字符之间', trigger: 'blur' }
  ]
}

// ===== 查询部门列表 =====
const queryDeptList = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 1) {
      rawList.value = res.data.data || []
      filterList()
    } else {
      ElMessage.error(res.data.msg || '查询部门失败')
    }
  } catch (err) {
    console.error('查询部门失败：', err)
    ElMessage.error('查询失败，请确认后端服务已启动（http://localhost:8080）')
  }
}

// 后端 GET /depts 不支持按名称查询，所以过滤放在前端做
const filterList = () => {
  const keyword = searchName.value.trim()
  deptList.value = keyword
    ? rawList.value.filter(dept => dept.name && dept.name.includes(keyword))
    : rawList.value
}

const clearSearch = () => {
  searchName.value = ''
  filterList()
}

// ===== 新增部门：打开弹窗 =====
const openAddDialog = () => {
  dialogTitle.value = '新增部门'
  form.id = null
  form.name = ''
  dialogVisible.value = true
  // 清掉上一次的校验红字（弹窗刚开时表单还没渲染，加个延时更稳）
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

// ===== 编辑部门：打开弹窗并回显数据 =====
const openEditDialog = (row) => {
  dialogTitle.value = '编辑部门'
  form.id = row.id
  form.name = row.name
  dialogVisible.value = true
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

// ===== 提交表单：有 id 走修改，没 id 走新增 =====
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const res = form.id
        ? await request.put(API, { id: form.id, name: form.name })
        : await request.post(API, { name: form.name })

      if (res.data.code === 1) {
        ElMessage.success(form.id ? '修改成功' : '新增成功')
        dialogVisible.value = false
        queryDeptList()
      } else {
        ElMessage.error(res.data.msg || '操作失败')
      }
    } catch (err) {
      console.error('保存部门失败：', err)
      ElMessage.error('操作失败，请确认后端服务已启动')
    }
  })
}

// ===== 删除部门 =====
const deleteDept = (row) => {
  ElMessageBox.confirm(`确定要删除【${row.name}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        // 后端接口是 DELETE /depts?id=x，参数放在 query 里
        const res = await request.delete(API, { params: { id: row.id } })
        if (res.data.code === 1) {
          ElMessage.success('删除成功')
          queryDeptList()
        } else {
          ElMessage.error(res.data.msg || '删除失败，该部门下可能还有员工')
        }
      } catch (err) {
        console.error('删除部门失败：', err)
        ElMessage.error('删除失败，请确认后端服务已启动')
      }
    })
    // 用户点了取消，什么都不做
    .catch(() => {})
}

// ===== 时间格式化：2026-08-09T18:46:42 → 2026-08-09 18:46:42 =====
const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ')
}

onMounted(() => {
  queryDeptList()
})
</script>

<template>
  <el-card class="dept-card">
    <!-- 顶部工具栏：左边搜索、右边新增 -->
    <div class="toolbar">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="部门名称">
          <el-input
            v-model="searchName"
            placeholder="请输入部门名称"
            clearable
            style="width: 200px"
            @keyup.enter="filterList"
            @clear="filterList"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="filterList">查询</el-button>
          <el-button @click="clearSearch">清空</el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" @click="openAddDialog">+ 新增部门</el-button>
    </div>

    <!-- 部门列表 -->
    <el-table :data="deptList" border style="width: 100%">
      <el-table-column type="index" label="序号" width="80" align="center" />

      <el-table-column prop="name" label="部门名称" align="center" />

      <el-table-column label="最后操作时间" align="center">
        <template #default="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="deleteDept(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 新增 / 编辑 弹窗（两者共用一个表单） -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入部门名称" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }
</style>
