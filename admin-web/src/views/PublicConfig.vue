<template>
  <div class="public-config-page">
    <div class="page-header">
      <h2>公共配置</h2>
      <p>维护小程序端公共展示信息，保存后约 30 分钟自动生效</p>
    </div>

    <div v-loading="loading" class="page-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" label-position="right" style="max-width: 820px">
        <el-form-item label="应用名称" prop="appName">
          <el-input v-model="form.appName" placeholder="请输入应用名称" maxlength="30" show-word-limit />
        </el-form-item>
        <el-form-item label="Slogan" prop="slogan">
          <el-input v-model="form.slogan" placeholder="请输入 slogan" maxlength="60" show-word-limit />
        </el-form-item>
        <el-form-item label="版本名称" prop="versionName">
          <el-input v-model="form.versionName" placeholder="如 v1.0.0" maxlength="20" />
        </el-form-item>
        <el-form-item label="关于标题" prop="aboutTitle">
          <el-input v-model="form.aboutTitle" placeholder="请输入关于页标题" maxlength="60" />
        </el-form-item>
        <el-form-item label="关于内容" prop="aboutContent">
          <el-input v-model="form.aboutContent" type="textarea" :rows="6" placeholder="请输入关于页正文" />
        </el-form-item>
        <el-form-item label="反馈开关" prop="feedbackEnabled">
          <el-switch v-model="form.feedbackEnabled" />
          <span class="form-hint">{{ form.feedbackEnabled ? '已开启反馈入口' : '已关闭反馈入口' }}</span>
        </el-form-item>
        <el-form-item label="反馈提示语" prop="feedbackHint">
          <el-input v-model="form.feedbackHint" placeholder="请输入反馈提示语" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="联系方式" prop="contactText">
          <el-input v-model="form.contactText" placeholder="请输入联系方式" maxlength="200" />
        </el-form-item>
        <el-form-item label="公告" prop="announcement">
          <el-input v-model="form.announcement" type="textarea" :rows="4" placeholder="请输入公告内容" maxlength="500" show-word-limit />
        </el-form-item>

        <el-form-item label-width="0" class="form-actions">
          <el-button @click="loadConfig">重置</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getPublicConfig, updatePublicConfig } from '@/api/publicConfig'
import type { PublicConfigSaveRequest } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<PublicConfigSaveRequest>({
  appName: '',
  slogan: '',
  versionName: '',
  aboutTitle: '',
  aboutContent: '',
  feedbackEnabled: true,
  feedbackHint: '',
  contactText: '',
  announcement: ''
})

const rules: FormRules = {
  appName: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
  slogan: [{ required: true, message: '请输入 slogan', trigger: 'blur' }],
  versionName: [{ required: true, message: '请输入版本名称', trigger: 'blur' }],
  aboutTitle: [{ required: true, message: '请输入关于标题', trigger: 'blur' }],
  aboutContent: [{ required: true, message: '请输入关于内容', trigger: 'blur' }],
  feedbackHint: [{ required: true, message: '请输入反馈提示语', trigger: 'blur' }],
  contactText: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

async function loadConfig() {
  loading.value = true
  try {
    const data = await getPublicConfig()
    Object.assign(form, {
      appName: data.appName || '',
      slogan: data.slogan || '',
      versionName: data.versionName || '',
      aboutTitle: data.aboutTitle || '',
      aboutContent: data.aboutContent || '',
      feedbackEnabled: !!data.feedbackEnabled,
      feedbackHint: data.feedbackHint || '',
      contactText: data.contactText || '',
      announcement: data.announcement || ''
    })
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await updatePublicConfig({ ...form })
      ElMessage.success('配置已更新，小程序端缓存 30 分钟后自动刷新')
    } catch {
      // ignore
    } finally {
      submitting.value = false
    }
  })
}

onMounted(loadConfig)
</script>

<style scoped>
.public-config-page {
  padding: 4px;
}

.page-header {
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 600;
  color: #1f2329;
}

.page-header p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.form-hint {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
}

.form-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
