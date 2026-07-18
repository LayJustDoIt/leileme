<template>
  <div class="feedback-list-page">
    <div class="page-header">
      <h2>反馈管理</h2>
      <p>查看与处理用户反馈，支持状态流转与备注</p>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索反馈内容/联系方式" clearable @keyup.enter="handleSearch" />
      <el-select v-model="query.status" placeholder="全部状态" clearable @change="handleSearch">
        <el-option v-for="item in FEEDBACK_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
    </div>

    <div class="page-card">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="反馈内容" prop="content" min-width="280" show-overflow-tooltip />
        <el-table-column label="联系方式" prop="contact" width="160">
          <template #default="{ row }">{{ row.contact || '-' }}</template>
        </el-table-column>
        <el-table-column label="页面路径" prop="pagePath" width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ row.pagePath || '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" prop="submittedAt" width="170" align="center" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getFeedbackStatusTagType(row.status)" size="small">{{ getFeedbackStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row as AdminFeedbackVO)">查看详情</el-button>
            <el-button v-if="row.status !== 'PROCESSED'" link type="success" size="small" @click="handleUpdateStatus(row as AdminFeedbackVO, 'PROCESSED')">标记已处理</el-button>
            <el-button v-if="row.status !== 'IGNORED'" link type="warning" size="small" @click="handleUpdateStatus(row as AdminFeedbackVO, 'IGNORED')">忽略</el-button>
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

    <el-dialog v-model="detailVisible" title="反馈详情" width="600px" :close-on-click-modal="false">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="ID">{{ current.id }}</el-descriptions-item>
        <el-descriptions-item label="反馈内容">
          <div class="feedback-content">{{ current.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ current.contact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="页面路径">{{ current.pagePath || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getFeedbackStatusTagType(current.status)" size="small">{{ getFeedbackStatusLabel(current.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ current.submittedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="管理员备注">
          <el-input v-model="remarkDraft" type="textarea" :rows="3" placeholder="请输入处理备注" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSaveRemark">保存备注</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listFeedbacks, updateFeedbackStatus, updateFeedbackNote } from '@/api/feedback'
import { FEEDBACK_STATUS_OPTIONS, getFeedbackStatusLabel, getFeedbackStatusTagType } from '@/constants/enums'
import type { AdminFeedbackVO, FeedbackListQuery, FeedbackStatus } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const list = ref<AdminFeedbackVO[]>([])
const total = ref(0)

const query = reactive<FeedbackListQuery>({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const detailVisible = ref(false)
const current = ref<AdminFeedbackVO | null>(null)
const remarkDraft = ref('')

async function loadList() {
  loading.value = true
  try {
    const res = await listFeedbacks(query)
    list.value = res.list || []
    total.value = res.total || 0
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function handleReset() {
  query.keyword = ''
  query.status = undefined
  query.page = 1
  loadList()
}

function handleView(row: AdminFeedbackVO) {
  current.value = row
  remarkDraft.value = row.adminNote || ''
  detailVisible.value = true
}

async function handleUpdateStatus(row: AdminFeedbackVO, status: FeedbackStatus) {
  const action = status === 'PROCESSED' ? '标记为已处理' : '忽略'
  try {
    await ElMessageBox.confirm(`确定要将此反馈${action}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await updateFeedbackStatus(row.id, status)
    ElMessage.success('状态已更新')
    loadList()
  } catch {
    // ignore
  }
}

async function handleSaveRemark() {
  if (!current.value) return
  submitting.value = true
  try {
    await updateFeedbackNote(current.value.id, remarkDraft.value)
    ElMessage.success('备注已保存')
    detailVisible.value = false
    loadList()
  } catch {
    // ignore
  } finally {
    submitting.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.feedback-list-page {
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

.feedback-content {
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 240px;
  overflow-y: auto;
}
</style>
