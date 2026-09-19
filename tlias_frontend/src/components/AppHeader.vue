<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()

// 登录成功后用户名存在 localStorage 里，这里读出来显示
const nickname = ref(localStorage.getItem('loginName') || '未登录')

// ==================== 修改密码 ====================
const pwdDialogVisible = ref(false)
const pwdFormRef = ref()
const saving = ref(false)

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6 到 20 个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      // 自定义校验规则：两次输入必须一致
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 打开修改密码弹窗
const changePassword = () => {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdDialogVisible.value = true
  setTimeout(() => pwdFormRef.value?.clearValidate(), 0)
}

// 提交修改密码
const submitPassword = () => {
  pwdFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      // 后端会从 token 里解析出当前登录用户，所以只需要传两个密码
      const res = await request.put('/emps/password', {
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })

      if (res.data.code === 1) {
        ElMessage.success('密码修改成功，请重新登录')
        pwdDialogVisible.value = false
        // 密码已变，清掉本地登录信息回到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('loginName')
        setTimeout(() => router.push('/login'), 600)
      } else {
        ElMessage.error(res.data.msg || '修改失败')
      }
    } catch (err) {
      console.error('修改密码失败：', err)
      // 原密码错误时后端会返回具体原因，优先展示
      ElMessage.error(err.response?.data?.msg || '修改失败，请确认后端服务已启动')
    } finally {
      saving.value = false
    }
  })
}

// ==================== 退出登录 ====================
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('loginName')
      ElMessage.success('已退出登录')
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<template>
  <div class="header">
    <!-- 左上角：用户名 + 系统标题 -->
    <div class="header-left">
      <span class="nickname">{{ nickname }}</span>
      <span class="title">tlias 智能学习辅助系统</span>
    </div>

    <!-- 右上角：修改密码 / 退出登录 -->
    <div class="header-right">
      <a class="header-link" href="javascript:void(0);" @click="changePassword">
        <svg viewBox="0 0 1024 1024" aria-hidden="true">
          <path d="M832 320h-64v-64a256 256 0 0 0-512 0v64H192a32 32 0 0 0-32 32v480a32 32 0 0 0 32 32h640a32 32 0 0 0 32-32V352a32 32 0 0 0-32-32z m-512-64a192 192 0 0 1 384 0v64H320z m448 544H256V384h512z"/>
        </svg>
        <span>修改密码</span>
      </a>

      <span class="divider"></span>

      <a class="header-link" href="javascript:void(0);" @click="logout">
        <svg viewBox="0 0 1024 1024" aria-hidden="true">
          <path d="M512 64a448 448 0 1 0 448 448 32 32 0 0 0-64 0 384 384 0 1 1-384-384 32 32 0 0 0 0-64z"/>
          <path d="M800 480H384a32 32 0 0 0 0 64h416l-96 96a32 32 0 0 0 45.248 45.248l149.024-149.024a32 32 0 0 0 0-45.248L749.248 341.752A32 32 0 0 0 704 386.976z"/>
        </svg>
        <span>退出登录</span>
      </a>
    </div>
  </div>

  <!-- 修改密码弹窗 -->
  <el-dialog v-model="pwdDialogVisible" title="修改密码" width="420px">
    <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
      </el-form-item>

      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="pwdDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submitPassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
  .header {
    height: 50px;
    background-color: #4a4a4a;
    display: flex;
    /* 主轴两端对齐：左侧信息左对齐、右侧操作右对齐 */
    justify-content: space-between;
    /* 交叉轴垂直居中：保证同一行内垂直居中 */
    align-items: center;
    padding: 0 20px;
  }

  /* 左上角区域：用户名 + 标题 */
  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  /* 登录用户名 */
  .nickname {
    color: #ffffff;
    font-size: 15px;
    font-weight: bold;
    padding-right: 12px;
    /* 与标题之间用一条竖线分隔 */
    border-right: 1px solid rgba(255, 255, 255, 0.35);
  }

  /* 系统标题 */
  .header .title {
    color: #ffffff;
    font-size: 24px;
    font-weight: bold;
    letter-spacing: 2px;
  }

  /* 右上角区域 */
  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .header-link {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #ffffff;
    font-size: 14px;
    text-decoration: none;
    cursor: pointer;
    transition: color 0.2s;
  }

  .header-link:hover {
    color: #ffd04b;
    text-decoration: underline;
  }

  .header-link svg {
    width: 16px;
    height: 16px;
    fill: currentColor;
  }

  /* 两个链接之间的竖线 */
  .divider {
    width: 1px;
    height: 16px;
    background-color: rgba(255, 255, 255, 0.35);
  }
</style>
