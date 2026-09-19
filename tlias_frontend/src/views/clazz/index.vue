<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

const API = '/clazzs'
const EMP_API = '/emps'

// 学科选项（对应库里的 subject 取值，需要扩展时在这里加）
const subjectOptions = [
  { value: 1, label: 'Java' },
  { value: 2, label: '前端' },
  { value: 3, label: '大数据' }
]

// ==================== 列表部分 ====================

const searchForm = reactive({
  name: '',
  dateRange: []   // [开课开始时间, 开课结束时间]，对应后端的 begin / end
})

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

// 查询班级列表
const search = async () => {
  try {
    const res = await request.get(API, {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        name: searchForm.name,
        // 日期范围是数组，拆成后端要的 begin / end；清空后是 null，要兜一下
        begin: searchForm.dateRange?.[0] || '',
        end: searchForm.dateRange?.[1] || ''
      }
    })

    if (res.data.code === 1) {
      tableData.value = res.data.data.rows || []
      total.value = res.data.data.total || 0
    } else {
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    console.error('查询班级失败：', err)
    ElMessage.error('查询失败，请确认后端服务已启动（http://localhost:8080）')
  }
}

const handleSearch = () => {
  page.value = 1
  search()
}

const clear = () => {
  searchForm.name = ''
  searchForm.dateRange = []
  handleSearch()
}

const handlePageChange = () => search()

const handleSizeChange = () => {
  page.value = 1
  search()
}

// 班级状态由后端返回（ClazzServiceImpl 里按开课 / 结课时间推导）
// 这里只负责把状态映射成标签颜色
const statusTagType = (status) => {
  if (status === '在读') return 'success'
  if (status === '已结课') return 'info'
  return 'warning'
}

// 删除班级：DELETE /clazzs/{id}
const deleteClazz = (row) => {
  ElMessageBox.confirm(`确定要删除【${row.name}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const res = await request.delete(`${API}/${row.id}`)
        if (res.data.code === 1) {
          ElMessage.success('删除成功')
          if (tableData.value.length === 1 && page.value > 1) {
            page.value = page.value - 1
          }
          search()
        } else {
          ElMessage.error(res.data.msg || '删除失败')
        }
      } catch (err) {
        console.error('删除班级失败：', err)
        ElMessage.error(err.response?.data?.msg || '删除失败，请确认后端服务已启动')
      }
    })
    .catch(() => {})
}

// ==================== 新增 / 编辑（共用弹窗） ====================

// 班主任下拉数据：取所有员工里职位为「班主任」的
const masterOptions = ref([])

const loadMasters = async () => {
  try {
    const res = await request.get(EMP_API, { params: { page: 1, pageSize: 1000 } })
    if (res.data.code === 1) {
      masterOptions.value = (res.data.data.rows || []).filter(emp => emp.job === 1)
    }
  } catch (err) {
    console.error('加载班主任列表失败：', err)
  }
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增班级')
const saving = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  room: '',
  beginDate: '',
  endDate: '',
  masterId: '',
  subject: ''
})

const rules = {
  name: [
    { required: true, message: '请输入班级名称', trigger: 'blur' },
    { min: 2, max: 30, message: '班级名称长度需在 2 到 30 个字符之间', trigger: 'blur' }
  ],
  room: [{ required: true, message: '请输入班级教室', trigger: 'blur' }],
  beginDate: [{ required: true, message: '请选择开课时间', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结课时间', trigger: 'change' }],
  masterId: [{ required: true, message: '请选择班主任', trigger: 'change' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }]
}

// 打开新增弹窗
const openAddDialog = () => {
  Object.assign(form, {
    id: null,
    name: '',
    room: '',
    beginDate: '',
    endDate: '',
    masterId: '',
    subject: ''
  })
  dialogTitle.value = '新增班级'
  dialogVisible.value = true
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

// 打开编辑弹窗：按 id 查详情回显
const editClazz = async (row) => {
  try {
    const res = await request.get(`${API}/${row.id}`)
    if (res.data.code === 1) {
      const clazz = res.data.data
      Object.assign(form, {
        id: clazz.id,
        name: clazz.name,
        room: clazz.room,
        beginDate: clazz.beginDate,
        endDate: clazz.endDate,
        masterId: clazz.masterId,
        subject: clazz.subject
      })
      dialogTitle.value = '编辑班级'
      dialogVisible.value = true
      setTimeout(() => formRef.value?.clearValidate(), 0)
    } else {
      ElMessage.error(res.data.msg || '查询班级详情失败')
    }
  } catch (err) {
    console.error('查询班级详情失败：', err)
    ElMessage.error('查询班级详情失败，请确认后端服务已启动')
  }
}

// 保存：有 id 走修改，没 id 走新增
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    const isEdit = form.id !== null
    try {
      const res = isEdit
        ? await request.put(API, form)
        : await request.post(API, form)

      if (res.data.code === 1) {
        ElMessage.success(isEdit ? '修改成功' : '新增成功')
        dialogVisible.value = false
        search()
      } else {
        ElMessage.error(res.data.msg || (isEdit ? '修改失败' : '新增失败'))
      }
    } catch (err) {
      console.error('保存班级失败：', err)
      ElMessage.error(err.response?.data?.msg || '保存失败，请确认后端服务已启动')
    } finally {
      saving.value = false
    }
  })
}

onMounted(() => {
  loadMasters()
  search()
})
</script>

<template>
  <el-card>
    <!-- ==================== 搜索 + 新增 ==================== -->
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="班级名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入班级名称"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item label="开课时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 260px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="clear">清空</el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" @click="openAddDialog">+ 新增班级</el-button>
    </div>

    <!-- ==================== 班级列表 ==================== -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="班级名称" align="center" />
      <el-table-column prop="room" label="班级教室" align="center" />
      <el-table-column prop="masterName" label="班主任" align="center" />

      <el-table-column prop="beginDate" label="开课时间" align="center" />
      <el-table-column prop="endDate" label="结课时间" align="center" />

      <el-table-column label="学科" align="center">
        <template #default="scope">
          <span v-for="item in subjectOptions" :key="item.value">
            <span v-if="scope.row.subject === item.value">{{ item.label }}</span>
          </span>
        </template>
      </el-table-column>

      <el-table-column label="状态" align="center">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="160" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="editClazz(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="deleteClazz(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- ==================== 分页 ==================== -->
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

  <!-- ==================== 新增 / 编辑弹窗 ==================== -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="班级名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入班级名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班级教室" prop="room">
            <el-input v-model="form.room" placeholder="请输入班级教室" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="开课时间" prop="beginDate">
            <el-date-picker
              v-model="form.beginDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择开课时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结课时间" prop="endDate">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择结课时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="班主任" prop="masterId">
            <el-select v-model="form.masterId" placeholder="请选择班主任" style="width: 100%">
              <el-option
                v-for="emp in masterOptions"
                :key="emp.id"
                :label="emp.name"
                :value="emp.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学科" prop="subject">
            <el-select v-model="form.subject" placeholder="请选择学科" style="width: 100%">
              <el-option
                v-for="item in subjectOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .pagination {
    margin-top: 16px;
    justify-content: flex-end;
  }
</style>
