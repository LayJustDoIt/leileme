<template>
  <div class="recommendation-list-page">
    <div class="page-header">
      <h2>推荐位管理</h2>
      <p>管理首页推荐位内容，支持设置生效时间与启停</p>
    </div>

    <div class="search-bar">
      <el-input v-model="query.slotCode" placeholder="搜索槽位标识" clearable @keyup.enter="handleSearch" />
      <el-select v-model="query.status" placeholder="全部状态" clearable @change="handleSearch">
        <el-option v-for="item in ENABLED_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      <div class="search-spacer" />
      <el-button type="primary" :icon="Plus" @click="handleAdd">新建推荐</el-button>
    </div>

    <div class="page-card">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="槽位" prop="slotCode" width="140">
          <template #default="{ row }">{{ getRecommendationSlotLabel(row.slotCode) }}</template>
        </el-table-column>
        <el-table-column label="内容ID" prop="contentId" width="90" align="center">
          <template #default="{ row }">{{ row.contentId || '-' }}</template>
        </el-table-column>
        <el-table-column label="分类" width="120" align="center">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column label="标题覆盖" prop="titleOverride" width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ row.titleOverride || '-' }}</template>
        </el-table-column>
        <el-table-column label="图片覆盖" prop="imageOverride" width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ row.imageOverride || '-' }}</template>
        </el-table-column>
        <el-table-column label="排序" prop="sortNo" width="80" align="center" />
        <el-table-column label="生效时间" width="320" align="center">
          <template #default="{ row }">
            <span v-if="row.startAt || row.endAt">
              {{ row.startAt || '-' }} ~ {{ row.endAt || '-' }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getEnabledStatusTagType(row.status)" size="small">{{ getEnabledStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row as AdminRecommendationVO)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleToggle(row as AdminRecommendationVO, 0)">停用</el-button>
            <el-button v-else link type="success" size="small" @click="handleToggle(row as AdminRecommendationVO, 1)">启用</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row as AdminRecommendationVO)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadList"
          @current-change="loadList"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑推荐' : '新建推荐'" width="560px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="editing" :rules="rules" label-width="100px">
        <el-form-item label="槽位" prop="slotCode">
          <el-select v-model="editing.slotCode" placeholder="请选择槽位" filterable allow-create style="width: 100%">
            <el-option v-for="item in RECOMMENDATION_SLOT_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容ID">
          <el-input-number v-model="editing.contentId" :min="1" :controls="false" style="width: 100%" placeholder="请输入关联内容ID" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editing.categoryId" placeholder="请选择分类" clearable filterable style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题覆盖">
          <el-input v-model="editing.titleOverride" placeholder="覆盖显示的标题（可选）" />
        </el-form-item>
        <el-form-item label="图片覆盖">
          <el-input v-model="editing.imageOverride" placeholder="覆盖显示的图片URL（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editing.sortNo" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生效开始">
          <el-date-picker v-model="editing.startAt" type="datetime" placeholder="选择生效开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生效结束">
          <el-date-picker v-model="editing.endAt" type="datetime" placeholder="选择生效结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editing.status" style="width: 100%">
            <el-option v-for="item in ENABLED_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { listRecommendations, createRecommendation, updateRecommendation, deleteRecommendation, toggleRecommendationStatus, type RecommendationListQuery } from '@/api/recommendation'
import { listAllCategories } from '@/api/category'
import { ENABLED_STATUS_OPTIONS, RECOMMENDATION_SLOT_OPTIONS, getEnabledStatusLabel, getEnabledStatusTagType, getRecommendationSlotLabel } from '@/constants/enums'
import type { AdminRecommendationVO, AdminRecommendationSaveRequest, RecommendationStatus, AdminCategoryVO } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const list = ref<AdminRecommendationVO[]>([])
const total = ref(0)
const categories = ref<AdminCategoryVO[]>([])

const query = reactive<RecommendationListQuery>({
  page: 1,
  size: 10,
  slotCode: '',
  status: undefined
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const editing = reactive<AdminRecommendationSaveRequest>({
  id: undefined,
  slotCode: '',
  contentId: undefined,
  categoryId: undefined,
  titleOverride: '',
  imageOverride: '',
  sortNo: 0,
  startAt: '',
  endAt: '',
  status: 1
})

const rules: FormRules = {
  slotCode: [{ required: true, message: '请输入槽位标识', trigger: 'blur' }]
}

function getCategoryName(id?: number): string {
  if (!id) return '-'
  const c = categories.value.find((item) => item.id === id)
  return c?.categoryName || '-'
}

async function loadList() {
  loading.value = true
  try {
    const res = await listRecommendations(query)
    list.value = res.list || []
    total.value = res.total || 0
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = await listAllCategories()
  } catch {
    // ignore
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function handleReset() {
  query.slotCode = ''
  query.status = undefined
  query.page = 1
  loadList()
}

function resetEditing() {
  editing.id = undefined
  editing.slotCode = ''
  editing.contentId = undefined
  editing.categoryId = undefined
  editing.titleOverride = ''
  editing.imageOverride = ''
  editing.sortNo = 0
  editing.startAt = ''
  editing.endAt = ''
  editing.status = 1
}

function handleAdd() {
  resetEditing()
  dialogVisible.value = true
}

function handleEdit(row: AdminRecommendationVO) {
  resetEditing()
  editing.id = row.id
  editing.slotCode = row.slotCode
  editing.contentId = row.contentId
  editing.categoryId = row.categoryId
  editing.titleOverride = row.titleOverride || ''
  editing.imageOverride = row.imageOverride || ''
  editing.sortNo = row.sortNo ?? 0
  editing.startAt = row.startAt || ''
  editing.endAt = row.endAt || ''
  editing.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: AdminRecommendationSaveRequest = { ...editing }
      if (editing.id) {
        await updateRecommendation(editing.id, payload)
        ElMessage.success('更新成功')
      } else {
        await createRecommendation(payload)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadList()
    } catch {
      // ignore
    } finally {
      submitting.value = false
    }
  })
}

async function handleToggle(row: AdminRecommendationVO, status: RecommendationStatus) {
  try {
    await toggleRecommendationStatus(row.id, status)
    ElMessage.success(status === 1 ? '已启用' : '已停用')
    loadList()
  } catch {
    // ignore
  }
}

async function handleDelete(row: AdminRecommendationVO) {
  try {
    await ElMessageBox.confirm(`确定要删除推荐位「${row.slotCode}」吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }
  try {
    await deleteRecommendation(row.id)
    ElMessage.success('删除成功')
    if (list.value.length === 1 && query.page! > 1) {
      query.page = query.page! - 1
    }
    loadList()
  } catch {
    // ignore
  }
}

onMounted(() => {
  loadCategories()
  loadList()
})
</script>

<style scoped>
.recommendation-list-page {
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

.search-spacer {
  flex: 1;
}
</style>
