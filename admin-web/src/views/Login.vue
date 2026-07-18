<template>
  <div class="login-page">
    <div class="login-bg" />
    <div class="login-card">
      <div class="login-header">
        <img src="data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48'><rect width='48' height='48' rx='10' fill='%23409EFF'/><text x='24' y='32' font-size='24' font-weight='700' fill='white' text-anchor='middle' font-family='PingFang SC, sans-serif'>累</text></svg>" alt="logo" />
        <div class="login-title">
          <h1>累了么管理后台</h1>
          <p>LeiLeiMe Admin Console</p>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" @keyup.enter="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">© {{ year }} 累了么 · All Rights Reserved</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const year = computed(() => new Date().getFullYear())

const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login({
        username: form.username.trim(),
        password: form.password
      })
      authStore.setLogin(res)
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/dashboard'
      router.replace(redirect)
    } catch {
      // 错误已在拦截器中提示
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
}

.login-bg {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.08) 0, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.06) 0, transparent 40%);
  pointer-events: none;
}

.login-card {
  position: relative;
  width: 380px;
  background: #fff;
  border-radius: 12px;
  padding: 36px 32px 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.login-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}

.login-header img {
  width: 48px;
  height: 48px;
  border-radius: 10px;
}

.login-title h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2329;
}

.login-title p {
  margin: 2px 0 0;
  font-size: 12px;
  color: #909399;
  letter-spacing: 0.5px;
}

.login-btn {
  width: 100%;
  letter-spacing: 4px;
}

.login-footer {
  text-align: center;
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 16px;
}
</style>
