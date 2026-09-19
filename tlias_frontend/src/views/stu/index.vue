<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

const API = '/students'
const CLAZZ_API = '/clazzs'

// 学历选项（对应后端 degree 字段注释：1 初中 … 6 博士）
const degreeOptions = [
  { value: 1, label: '初中' },
  { value: 2, label: '高中' },
  { value: 3, label: '大专' },
  { value: 4, label: '本科' },
  { value: 5, label: '硕士' },
  { value: 6, label: '博士' }
]

// 根据编号取学历名称
const degreeLabel = (value) => {
  const item = degreeOptions.find(d => d.value === value)
  return item ? item.label : ''
}

// ==================== 列表部分 ====================

const searchForm = reactive({
  name: '',
  degree: '',
  clazzId: ''
})

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

// 班级下拉数据（查询条件和表单都用它）
const clazzOptions = ref([])

const loadClazzOptions = async () => {
  try {
    // 这个接口专门返回全部班级，不分页
    const res = await request.get(`${CLAZZ_API}/list`)
    if (res.data.code === 1) {
      clazzOptions.value = res.data.data || []
    }
  } catch (err) {
    console.error('加载班级列表失败：', err)
  }
}

// 查询学员列表
const search = async () => {
  try {
    const res = await request.get(API, {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        name: searchForm.name,
        degree: searchForm.degree,
        clazzId: searchForm.clazzId
      }
    })

    if (res.data.code === 1) {
      tableData.value = res.data.data.rows || []
      total.value = res.data.data.total || 0
    } else {
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    console.error('查询学员失败：', err)
    ElMessage.error('查询失败，请确认后端服务已启动（http://localhost:8080）')
  }
}

const handleSearch = () => {
  page.value = 1
  search()
}

const clear = () => {
  searchForm.name = ''
  searchForm.degree = ''
  searchForm.clazzId = ''
  handleSearch()
}

const handlePageChange = () => search()

const handleSizeChange = () => {
  page.value = 1
  search()
}

// 删除学员：DELETE /students/{ids}（路径参数，支持多个 id 逗号分隔）
const deleteStu = (row) => {
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
        console.error('删除学员失败：', err)
        ElMessage.error(err.response?.data?.msg || '删除失败，请确认后端服务已启动')
      }
    })
    .catch(() => {})
}

// 违纪处理：弹出输入框填扣分，PUT /students/violation/{id}/{score}
const handleViolation = (row) => {
  ElMessageBox.prompt(`为【${row.name}】录入违纪扣分`, '违纪处理', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入正整数',
    inputValue: '1'
  })
    .then(async ({ value }) => {
      try {
        const res = await request.put(`${API}/violation/${row.id}/${value}`)
        if (res.data.code === 1) {
          ElMessage.success('违纪处理完成')
          search()
        } else {
          ElMessage.error(res.data.msg || '违纪处理失败')
        }
      } catch (err) {
        console.error('违纪处理失败：', err)
        ElMessage.error('违纪处理失败，请确认后端服务已启动')
      }
    })
    .catch(() => {})
}

// ==================== 新增 / 编辑（共用弹窗） ====================

const dialogVisible = ref(false)
const dialogTitle = ref('新增学员')
const saving = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  no: '',
  gender: '',
  idCard: '',
  phone: '',
  isCollege: '',
  address: '',
  degree: '',
  graduationDate: '',
  clazzId: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  no: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    {
      pattern: /^\d{17}[\dXx]$/,
      message: '身份证号格式不正确（18 位）',
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  isCollege: [{ required: true, message: '请选择是否来自院校', trigger: 'change' }],
  address: [{ required: true, message: '请输入联系地址', trigger: 'blur' }],
  degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
  graduationDate: [{ required: true, message: '请选择毕业时间', trigger: 'change' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

// 打开新增弹窗
const openAddDialog = () => {
  Object.assign(form, {
    id: null,
    name: '',
    no: '',
    gender: '',
    idCard: '',
    phone: '',
    isCollege: '',
    address: '',
    degree: '',
    graduationDate: '',
    clazzId: ''
  })
  dialogTitle.value = '新增学员'
  dialogVisible.value = true
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

// 打开编辑弹窗：按 id 查详情回显
const editStu = async (row) => {
  try {
    const res = await request.get(`${API}/${row.id}`)
    if (res.data.code === 1) {
      const stu = res.data.data
      Object.assign(form, {
        id: stu.id,
        name: stu.name,
        no: stu.no,
        gender: stu.gender,
        idCard: stu.idCard,
        phone: stu.phone,
        isCollege: stu.isCollege,
        address: stu.address,
        degree: stu.degree,
        graduationDate: stu.graduationDate,
        clazzId: stu.clazzId
      })
      dialogTitle.value = '编辑学员'
      dialogVisible.value = true
      setTimeout(() => formRef.value?.clearValidate(), 0)
    } else {
      ElMessage.error(res.data.msg || '查询学员详情失败')
    }
  } catch (err) {
    console.error('查询学员详情失败：', err)
    ElMessage.error('查询学员详情失败，请确认后端服务已启动')
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
      console.error('保存学员失败：', err)
      ElMessage.error(err.response?.data?.msg || '保存失败，请确认后端服务已启动')
    } finally {
      saving.value = false
    }
  })
}

onMounted(() => {
  loadClazzOptions()
  search()
})
</script>

<template>
  <el-card>
    <!-- ==================== 搜索 + 新增 ==================== -->
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item label="学历">
          <el-select v-model="searchForm.degree" placeholder="请选择" clearable style="width: 130px">
            <el-option
              v-for="item in degreeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="班级">
          <el-select v-model="searchForm.clazzId" placeholder="请选择" clearable style="width: 180px">
            <el-option
              v-for="clazz in clazzOptions"
              :key="clazz.id"
              :label="clazz.name"
              :value="clazz.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="clear">清空</el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" @click="openAddDialog">+ 新增学员</el-button>
    </div>

    <!-- ==================== 学员列表 ==================== -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="姓名" align="center" />
      <el-table-column prop="no" label="学号" align="center" />

      <el-table-column label="性别" align="center">
        <template #default="scope">
          <span v-if="scope.row.gender === 1">男</span>
          <span v-else-if="scope.row.gender === 2">女</span>
          <span v-else>未知</span>
        </template>
      </el-table-column>

      <el-table-column prop="phone" label="手机号" align="center" />
      <el-table-column prop="clazzName" label="班级" align="center" />

      <el-table-column label="学历" align="center">
        <template #default="scope">{{ degreeLabel(scope.row.degree) }}</template>
      </el-table-column>

      <el-table-column prop="violationCount" label="违纪次数" align="center" />
      <el-table-column prop="violationScore" label="违纪扣分" align="center" />

      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="editStu(scope.row)">编辑</el-button>
          <el-button type="warning" link @click="handleViolation(scope.row)">违纪</el-button>
          <el-button type="danger" link @click="deleteStu(scope.row)">删除</el-button>
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
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="760px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学号" prop="no">
            <el-input v-model="form.no" placeholder="请输入学号" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否院校" prop="isCollege">
            <el-select v-model="form.isCollege" placeholder="请选择" style="width: 100%">
              <el-option label="是" :value="1" />
              <el-option label="否" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="学历" prop="degree">
            <el-select v-model="form.degree" placeholder="请选择" style="width: 100%">
              <el-option
                v-for="item in degreeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="毕业时间" prop="graduationDate">
            <el-date-picker
              v-model="form.graduationDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择毕业时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="班级" prop="clazzId">
            <el-select v-model="form.clazzId" placeholder="请选择班级" style="width: 100%">
              <el-option
                v-for="clazz in clazzOptions"
                :key="clazz.id"
                :label="clazz.name"
                :value="clazz.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入联系地址" />
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
