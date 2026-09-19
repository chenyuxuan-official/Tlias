<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 后端接口：POST /login，请求体 { username, password }
      const res = await request.post('/login', loginForm)

      if (res.data.code === 1) {
        // 返回结构：{ id, username, name, token }
        const loginInfo = res.data.data
        localStorage.setItem('token', loginInfo.token)
        localStorage.setItem('loginName', loginInfo.name || loginInfo.username)

        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error(res.data.msg || '用户名或密码错误')
      }
    } catch (err) {
      console.error('登录失败：', err)
      ElMessage.error('登录失败，请确认后端服务已启动（http://localhost:8080）')
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="login-title">tlias 智能学习辅助系统</h2>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        label-width="70px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
  .login-page {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    /* 简单渐变色背景，让登录页不那么单调 */
    background: linear-gradient(135deg, #4a4a4a 0%, #6b8e8b 100%);
  }

  .login-card {
    width: 400px;
    padding: 10px 20px;
  }

  .login-title {
    text-align: center;
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 24px;
  }
</style>
