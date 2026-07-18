<template>
  <div class="content-edit-page">
    <div class="page-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" link @click="router.back()">返回</el-button>
        <h2>{{ isEdit ? '编辑内容' : '新建内容' }}</h2>
      </div>
    </div>

    <div v-loading="loading" class="page-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" label-position="right">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="副标题">
              <el-input v-model="form.subtitle" placeholder="请输入副标题（可选）" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="摘要">
              <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入摘要" maxlength="300" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正文">
              <el-input v-model="form.body" type="textarea" :rows="10" placeholder="请输入正文内容" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面 URL">
              <el-input v-model="form.coverUrl" placeholder="请输入封面图链接" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="contentType">
              <el-select v-model="form.contentType" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="item in contentTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="请选择分类" clearable filterable style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签">
              <el-select v-model="form.tagIds" multiple placeholder="请选择标签" filterable style="width: 100%">
                <el-option v-for="t in tags" :key="t.id" :label="t.tagName" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源名称">
              <el-input v-model="form.sourceName" placeholder="请输入来源名称（可选）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源链接">
              <el-input v-model="form.sourceUrl" placeholder="请输入来源链接（可选）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关键词">
              <el-input v-model="form.searchKeywords" placeholder="多个关键词以逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="热度">
              <el-input-number v-model="form.hotScore" :min="0" :max="999999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="置顶">
              <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label-width="0" class="form-actions">
          <el-button @click="router.back()">取消</el-button>
          <el-button :loading="submitting && saveStatus === 0" @click="handleSave(0)">保存草稿</el-button>
          <el-button type="primary" :loading="submitting && saveStatus === 1" @click="handleSave(1)">{{ isEdit ? '保存并发布' : '创建并发布' }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getContent, createContent, updateContent } from '@/api/content'
import { listAllCategories } from '@/api/category'
import { listAllTags } from '@/api/tag'
import type {
  AdminContentSaveRequest,
  AdminCategoryVO,
  AdminTagVO,
  ContentType,
  ContentStatus
} from '@/types'
import { CONTENT_TYPE_OPTIONS } from '@/constants/enums'

const route = useRoute()
const router = useRouter()

const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)
const saveStatus = ref<ContentStatus>(0)

const categories = ref<AdminCategoryVO[]>([])
const tags = ref<AdminTagVO[]>([])

const isEdit = computed(() => !!route.params.id)
const contentId = computed(() => (route.params.id ? Number(route.params.id) : 0))

const form = reactive<AdminContentSaveRequest>({
  title: '',
  subtitle: '',
  summary: '',
  body: '',
  searchKeywords: '',
  coverUrl: '',
  sourceName: '',
  sourceUrl: '',
  authorName: '',
  contentType: 'HOT_NEWS',
  categoryId: undefined,
  status: 0,
  isOriginal: 0,
  isTop: 0,
  hotScore: 0,
  tagIds: []
})

const contentTypeOptions = CONTENT_TYPE_OPTIONS

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  contentType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

async function loadOptions() {
  try {
    const [catList, tagList] = await Promise.all([listAllCategories(), listAllTags()])
    categories.value = catList || []
    tags.value = tagList || []
  } catch {
    // ignore
  }
}

async function loadDetail() {
  if (!isEdit.value) return
  loading.value = true
  try {
    const data = await getContent(contentId.value)
    Object.assign(form, {
      title: data.title || '',
      subtitle: data.subtitle || '',
      summary: data.summary || '',
      body: data.body || '',
      searchKeywords: data.searchKeywords || '',
      coverUrl: data.coverUrl || '',
      sourceName: data.sourceName || '',
      sourceUrl: data.sourceUrl || '',
      authorName: data.authorName || '',
      contentType: data.contentType,
      categoryId: data.categoryId,
      tagIds: data.tagIds || [],
      isOriginal: data.isOriginal ?? 0,
      isTop: data.isTop ?? 0,
      hotScore: data.hotScore ?? 0,
      status: data.status
    })
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleSave(status: ContentStatus) {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    saveStatus.value = status
    const payload: AdminContentSaveRequest = {
      ...form,
      status,
      tagIds: form.tagIds || []
    }
    try {
      if (isEdit.value) {
        await updateContent(contentId.value, payload)
        ElMessage.success(status === 1 ? '保存并发布成功' : '保存成功')
      } else {
        await createContent(payload)
        ElMessage.success(status === 1 ? '创建并发布成功' : '创建草稿成功')
      }
      router.replace('/contents')
    } catch {
      // ignore
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadOptions()
  loadDetail()
})
</script>

<style scoped>
.content-edit-page {
  padding: 4px;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2329;
}

.form-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
