<template>
  <div class="content-list-page">
    <div class="page-header">
      <h2>内容管理</h2>
      <p>管理平台所有内容，包括热点、文案、AI 工具、学习、生活、随机等类型</p>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索标题/关键词" clearable @keyup.enter="handleSearch" />
      <el-select v-model="query.status" placeholder="全部状态" clearable @change="handleSearch">
        <el-option label="草稿" :value="0" />
        <el-option label="已发布" :value="1" />
        <el-option label="已下架" :value="2" />
      </el-select>
      <el-select v-model="query.contentType" placeholder="全部类型" clearable @change="handleSearch">
        <el-option v-for="item in CONTENT_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="query.categoryId" placeholder="全部分类" clearable filterable @change="handleSearch">
        <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      <div class="search-spacer" />
      <el-button type="primary" :icon="Plus" @click="router.push('/contents/new')">新建内容</el-button>
    </div>

    <div class="page-card">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="标题" prop="title" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.title }}</span>
            <el-tag v-if="row.isTop" type="danger" size="small" style="margin-left: 6px">置顶</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="contentType" width="120" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ getContentTypeLabel(row.contentType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="120" align="center">
          <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getContentStatusTagType(row.status)" size="small">{{ getContentStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="热度" prop="hotScore" width="80" align="center" />
        <el-table-column label="浏览数" prop="viewCount" width="90" align="center" />
        <el-table-column label="发布时间" prop="publishedAt" width="170" align="center">
          <template #default="{ row }">{{ row.publishedAt || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="router.push(`/contents/${row.id}/edit`)">编辑</el-button>
            <el-button v-if="row.status !== 1" link type="success" size="small" @click="handlePublish(row as AdminContentVO)">发布</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleOffline(row as AdminContentVO)">下架</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row as AdminContentVO)">删除</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { listContents, deleteContent, updateContentStatus } from '@/api/content'
import { listAllCategories } from '@/api/category'
import { CONTENT_TYPE_OPTIONS, getContentTypeLabel, getContentStatusLabel, getContentStatusTagType } from '@/constants/enums'
import type { AdminContentVO, ContentListQuery, AdminCategoryVO } from '@/types'

const router = useRouter()
const loading = ref(false)
const list = ref<AdminContentVO[]>([])
const total = ref(0)
const categories = ref<AdminCategoryVO[]>([])

const query = reactive<ContentListQuery>({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined,
  contentType: undefined,
  categoryId: undefined
})

function getCategoryName(id?: number): string {
  if (!id) return '-'
  const c = categories.value.find((item) => item.id === id)
  return c?.categoryName || '-'
}

async function loadList() {
  loading.value = true
  try {
    const res = await listContents(query)
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
  query.keyword = ''
  query.status = undefined
  query.contentType = undefined
  query.categoryId = undefined
  query.page = 1
  loadList()
}

async function handlePublish(row: AdminContentVO) {
  try {
    await ElMessageBox.confirm(`确定要发布「${row.title}」吗？`, '发布确认', {
      confirmButtonText: '确定发布',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await updateContentStatus(row.id, 1)
    ElMessage.success('发布成功')
    loadList()
  } catch {
    // ignore
  }
}

async function handleOffline(row: AdminContentVO) {
  try {
    await ElMessageBox.confirm(`确定要下架「${row.title}」吗？`, '下架确认', {
      confirmButtonText: '确定下架',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await updateContentStatus(row.id, 2)
    ElMessage.success('已下架')
    loadList()
  } catch {
    // ignore
  }
}

async function handleDelete(row: AdminContentVO) {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.title}」吗？删除后无法恢复。`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }
  try {
    await deleteContent(row.id)
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
.content-list-page {
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
