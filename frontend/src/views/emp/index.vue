<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

// 本地后端接口（request 的 baseURL 已是 /api）
const API = '/emps'
const DEPT_API = '/depts'

// ==================== 列表部分 ====================

// 搜索条件
const searchForm = reactive({
  name: '',
  gender: '',
  job: ''
})

// 分页
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref([])

// 查询员工列表
const search = async () => {
  try {
    const res = await request.get(API, {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        name: searchForm.name,
        gender: searchForm.gender,
        job: searchForm.job
      }
    })

    if (res.data.code === 1) {
      // 后端返回分页对象：{ total, rows }
      tableData.value = res.data.data.rows || []
      total.value = res.data.data.total || 0
    } else {
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (err) {
    console.error('查询员工列表失败：', err)
    ElMessage.error('查询失败，请确认后端服务已启动（http://localhost:8080）')
  }
}

// 条件变了要从第一页开始查
const handleSearch = () => {
  page.value = 1
  search()
}

const clear = () => {
  searchForm.name = ''
  searchForm.gender = ''
  searchForm.job = ''
  handleSearch()
}

const handlePageChange = () => search()

const handleSizeChange = () => {
  page.value = 1
  search()
}

// 删除员工
const deleteEmp = (row) => {
  ElMessageBox.confirm(`确定要删除【${row.name}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        // DELETE /emps?ids=1（支持批量，多个 id 用逗号分隔）
        const res = await request.delete(API, { params: { ids: row.id } })
        if (res.data.code === 1) {
          ElMessage.success('删除成功')
          // 删掉当前页最后一条时往前翻一页，避免停在空白页
          if (tableData.value.length === 1 && page.value > 1) {
            page.value = page.value - 1
          }
          search()
        } else {
          ElMessage.error(res.data.msg || '删除失败')
        }
      } catch (err) {
        console.error('删除员工失败：', err)
        ElMessage.error('删除失败，请确认后端服务已启动')
      }
    })
    .catch(() => {})
}

// ==================== 新增 / 编辑（共用同一个弹窗） ====================

// 部门下拉数据
const deptOptions = ref([])

const loadDeptOptions = async () => {
  try {
    const res = await request.get(DEPT_API)
    if (res.data.code === 1) {
      deptOptions.value = res.data.data || []
    }
  } catch (err) {
    console.error('加载部门列表失败：', err)
  }
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const saving = ref(false)
const formRef = ref()

// 表单数据（字段与后端 Emp 实体对应）
const form = reactive({
  id: null,
  username: '',
  name: '',
  gender: '',
  phone: '',
  job: '',
  salary: '',
  entryDate: '',
  deptId: '',
  exprList: []   // 工作经历，每项 { company, job, begin, end }
})

// 表单校验规则（长度限制参照数据库字段：username 20、name 10、phone 11 位唯一）
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度需在 2 到 20 个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度需在 2 到 10 个字符之间', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  job: [{ required: true, message: '请选择职位', trigger: 'change' }],
  salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }]
}

// 工作经历每行的校验规则（按路径绑定到 el-form-item 上）
const exprRules = {
  company: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  job: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  begin: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  end: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 打开新增弹窗：把表单重置成初始状态
const openAddDialog = () => {
  Object.assign(form, {
    id: null,
    username: '',
    name: '',
    gender: '',
    phone: '',
    job: '',
    salary: '',
    entryDate: '',
    deptId: '',
    exprList: []
  })
  dialogTitle.value = '新增员工'
  dialogVisible.value = true
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

// 打开编辑弹窗：先按 id 查详情（列表里的数据不含工作经历）
const editEmp = async (row) => {
  try {
    const res = await request.get(`${API}/${row.id}`)
    if (res.data.code === 1) {
      const emp = res.data.data

      Object.assign(form, {
        id: emp.id,
        username: emp.username,
        name: emp.name,
        gender: emp.gender,
        phone: emp.phone,
        job: emp.job,
        salary: emp.salary,
        entryDate: emp.entryDate,
        deptId: emp.deptId,
        // 只保留表单需要的字段；id 不用带，后端会先删旧再插新
        exprList: (emp.exprList || []).map(expr => ({
          company: expr.company,
          job: expr.job,
          begin: expr.begin,
          end: expr.end
        }))
      })

      dialogTitle.value = '编辑员工'
      dialogVisible.value = true
      // 弹窗刚打开时表单还没渲染，延时清理上一次的校验提示
      setTimeout(() => formRef.value?.clearValidate(), 0)
    } else {
      ElMessage.error(res.data.msg || '查询员工详情失败')
    }
  } catch (err) {
    console.error('查询员工详情失败：', err)
    ElMessage.error('查询员工详情失败，请确认后端服务已启动')
  }
}

// ===== 头像上传 =====
// el-upload 自带的请求不会携带 token，会被后端 TokenInterceptor 拦掉，
// 所以用 http-request 接管上传，走我们的 request 实例
const uploadAvatar = async (options) => {
  const fd = new FormData()
  // 字段名必须是 file，对应后端的 upload(MultipartFile file)
  fd.append('file', options.file)

  try {
    // request 实例的拦截器会自动加上 token 请求头
    const res = await request.post('/upload', fd)
    if (res.data.code === 1) {
      // 后端返回的是完整的 OSS 访问地址，例如
      // https://shanfu-test-bucket-123.oss-cn-beijing.aliyuncs.com/2026/09/uuid.jpg
      form.image = res.data.data
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.data.msg || '头像上传失败')
    }
  } catch (err) {
    console.error('头像上传失败：', err)
    ElMessage.error('头像上传失败，请检查 OSS 凭据与后端服务')
  }
}

// 添加一条空的工作经历
const addExpr = () => {
  form.exprList.push({ company: '', job: '', begin: '', end: '' })
}

// 删除某条工作经历
const removeExpr = (index) => {
  form.exprList.splice(index, 1)
}

// 保存：有 id 走修改，没 id 走新增
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    const isEdit = form.id !== null
    try {
      // 请求体都是整个 Emp 对象（含 exprList）
      const res = isEdit
        ? await request.put(API, form)     // 修改：PUT /emps
        : await request.post(API, form)    // 新增：POST /emps

      if (res.data.code === 1) {
        ElMessage.success(isEdit ? '修改成功' : '新增成功')
        dialogVisible.value = false
        search()
      } else {
        ElMessage.error(res.data.msg || (isEdit ? '修改失败' : '新增失败'))
      }
    } catch (err) {
      console.error('保存员工失败：', err)
      // 后端若返回了具体原因（如用户名、手机号重复），优先展示它
      ElMessage.error(err.response?.data?.msg || '保存失败，请确认后端服务已启动')
    } finally {
      saving.value = false
    }
  })
}

// 时间格式化：2026-08-09T18:46:42 → 2026-08-09 18:46:42
const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ')
}

onMounted(() => {
  loadDeptOptions()
  search()
})
</script>

<template>
  <el-card>
    <!-- ==================== 搜索表单 + 新增按钮 ==================== -->
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="searchForm.gender" placeholder="请选择" clearable style="width: 140px">
            <el-option label="男" value="1" />
            <el-option label="女" value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="职位">
          <el-select v-model="searchForm.job" placeholder="请选择" clearable style="width: 140px">
            <el-option label="班主任" value="1" />
            <el-option label="讲师" value="2" />
            <el-option label="学工主管" value="3" />
            <el-option label="教研主管" value="4" />
            <el-option label="咨询师" value="5" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="clear">清空</el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" @click="openAddDialog">+ 新增员工</el-button>
    </div>

    <!-- ==================== 员工列表 ==================== -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="姓名" align="center" />

      <el-table-column label="性别" align="center">
        <template #default="scope">
          <span v-if="scope.row.gender == 1">男</span>
          <span v-else-if="scope.row.gender == 2">女</span>
          <span v-else>未知</span>
        </template>
      </el-table-column>

      <el-table-column label="头像" align="center">
        <template #default="scope">
          <!-- el-image 加载失败时显示 error 插槽内容，避免破图 -->
          <el-image class="avatar" :src="scope.row.image" fit="cover">
            <template #error>
              <div class="avatar-error">无</div>
            </template>
          </el-image>
        </template>
      </el-table-column>

      <el-table-column label="职位" align="center">
        <template #default="scope">
          <span v-if="scope.row.job == 1">班主任</span>
          <span v-else-if="scope.row.job == 2">讲师</span>
          <span v-else-if="scope.row.job == 3">学工主管</span>
          <span v-else-if="scope.row.job == 4">教研主管</span>
          <span v-else-if="scope.row.job == 5">咨询师</span>
          <span v-else>其他</span>
        </template>
      </el-table-column>

      <el-table-column prop="entryDate" label="入职日期" align="center" />

      <el-table-column label="最后操作时间" align="center">
        <template #default="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="160" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="editEmp(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="deleteEmp(scope.row)">删除</el-button>
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

  <!-- ==================== 新增 / 编辑员工弹窗 ==================== -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="760px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
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
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="职位" prop="job">
            <el-select v-model="form.job" placeholder="请选择" style="width: 100%">
              <el-option label="班主任" :value="1" />
              <el-option label="讲师" :value="2" />
              <el-option label="学工主管" :value="3" />
              <el-option label="教研主管" :value="4" />
              <el-option label="咨询师" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="薪资" prop="salary">
            <el-input-number
              v-model="form.salary"
              :min="0"
              :max="1000000"
              controls-position="right"
              placeholder="请输入薪资"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="入职日期" prop="entryDate">
            <el-date-picker
              v-model="form.entryDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择入职日期"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="部门" prop="deptId">
            <el-select v-model="form.deptId" placeholder="请选择" style="width: 100%">
              <el-option
                v-for="dept in deptOptions"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
              />
            </el-select>
          </el-form-item>
        </el-col>

        <!-- 头像：选完文件立即上传到 OSS，成功后的地址存进 form.image -->
        <el-col :span="24">
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :http-request="uploadAvatar"
              accept="image/*"
            >
              <img v-if="form.image" :src="form.image" class="avatar-preview" alt="头像" />
              <div v-else class="avatar-uploader-tip">+ 上传头像</div>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- ===== 工作经历：可动态增删 ===== -->
      <el-divider content-position="left">工作经历</el-divider>

      <div class="expr-toolbar">
        <el-button type="primary" plain size="small" @click="addExpr">+ 添加工作经历</el-button>
      </div>

      <el-row v-for="(expr, index) in form.exprList" :key="index" :gutter="8" class="expr-row">
        <el-col :span="7">
          <el-form-item :prop="`exprList.${index}.company`" :rules="exprRules.company">
            <el-input v-model="expr.company" placeholder="公司名称" />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item :prop="`exprList.${index}.job`" :rules="exprRules.job">
            <el-input v-model="expr.job" placeholder="职位" />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item :prop="`exprList.${index}.begin`" :rules="exprRules.begin">
            <el-date-picker
              v-model="expr.begin"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="开始时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item :prop="`exprList.${index}.end`" :rules="exprRules.end">
            <el-date-picker
              v-model="expr.end"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-button type="danger" link @click="removeExpr(index)">删除</el-button>
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

  /* ===== 头像上传框 ===== */
  /* el-upload 内部元素属于子组件，scoped 样式要用 :deep() 才能命中 */
  .avatar-uploader :deep(.el-upload) {
    width: 80px;
    height: 80px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: border-color 0.2s;
  }

  .avatar-uploader :deep(.el-upload:hover) {
    border-color: #409eff;
  }

  .avatar-preview {
    width: 80px;
    height: 80px;
    object-fit: cover;
    display: block;
  }

  .avatar-uploader-tip {
    font-size: 12px;
    color: #909399;
    text-align: center;
    line-height: 1.4;
  }

  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    vertical-align: middle;
  }

  /* 图片加载失败时的占位样式 */
  .avatar-error {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: #f5f7fa;
    color: #c0c4cc;
    font-size: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .pagination {
    margin-top: 16px;
    justify-content: flex-end;
  }

  .expr-toolbar {
    margin-bottom: 12px;
  }

  .expr-row {
    margin-bottom: 4px;
  }
</style>
