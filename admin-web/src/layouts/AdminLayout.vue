<template>
  <el-container class="admin-layout">
    <el-aside :width="collapse ? '64px' : '220px'" class="admin-aside">
      <div class="logo">
        <img src="data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48'><rect width='48' height='48' rx='10' fill='%23409EFF'/><text x='24' y='32' font-size='24' font-weight='700' fill='white' text-anchor='middle' font-family='PingFang SC, sans-serif'>累</text></svg>" alt="logo" />
        <span v-show="!collapse" class="logo-text">累了么管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapse"
        :collapse-transition="false"
        background-color="#001529"
        text-color="#c9d1d9"
        active-text-color="#ffffff"
        router
        unique-opened
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>
        <el-menu-item index="/contents">
          <el-icon><Document /></el-icon>
          <template #title>内容管理</template>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Files /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/tags">
          <el-icon><PriceTag /></el-icon>
          <template #title>标签管理</template>
        </el-menu-item>
        <el-menu-item index="/recommendations">
          <el-icon><Star /></el-icon>
          <template #title>推荐位管理</template>
        </el-menu-item>
        <el-menu-item index="/hot-keywords">
          <el-icon><Search /></el-icon>
          <template #title>热门搜索词</template>
        </el-menu-item>
        <el-menu-item index="/public-config">
          <el-icon><Setting /></el-icon>
          <template #title>公共配置</template>
        </el-menu-item>
        <el-menu-item index="/feedbacks">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>反馈管理</template>
        </el-menu-item>
        <el-menu-item index="logout" @click.prevent="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <template #title>退出登录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="admin-main-container">
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="collapse = !collapse">
            <Fold v-if="!collapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="admin-info">
              <el-avatar :size="28" class="admin-avatar">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="admin-name">{{ displayName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="changePassword">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdSubmitting" @click="submitChangePassword">确认</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { changePassword } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const collapse = ref(false)

const activeMenu = computed(() => {
  // 内容编辑页高亮内容管理
  if (route.path.startsWith('/contents')) return '/contents'
  return route.path
})

const currentTitle = computed(() => (route.meta.title as string) || '管理后台')

const displayName = computed(() => authStore.adminInfo?.displayName || authStore.adminInfo?.username || '管理员')

// 修改密码
const pwdDialogVisible = ref(false)
const pwdSubmitting = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
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

function handleCommand(command: string) {
  if (command === 'changePassword') {
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    pwdDialogVisible.value = true
  } else if (command === 'logout') {
    handleLogout()
  }
}

async function submitChangePassword() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    pwdSubmitting.value = true
    try {
      await changePassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      pwdDialogVisible.value = false
      authStore.logout()
      router.replace('/login')
    } catch {
      // 错误已在拦截器中提示
    } finally {
      pwdSubmitting.value = false
    }
  })
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  authStore.logout()
  ElMessage.success('已退出登录')
  router.replace('/login')
}

onMounted(() => {
  authStore.loadFromStorage()
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-aside {
  background-color: #001529;
  transition: width 0.2s;
  overflow: hidden;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  color: #fff;
  gap: 10px;
  overflow: hidden;
}

.logo img {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
}

.admin-aside :deep(.el-menu) {
  border-right: none;
}

.admin-main-container {
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #5a5e66;
}

.collapse-btn:hover {
  color: #409eff;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  outline: none;
}

.admin-info:hover {
  background: #f5f7fa;
}

.admin-avatar {
  background: #409eff;
  color: #fff;
  font-weight: 600;
}

.admin-name {
  font-size: 14px;
  color: #303133;
}

.admin-main {
  background: #f5f7fa;
  padding: 16px;
  overflow-y: auto;
}
</style>
