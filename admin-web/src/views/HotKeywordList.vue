<template>
  <div class="hot-keyword-list-page">
    <div class="page-header">
      <h2>热门搜索词</h2>
      <p>管理小程序热门搜索关键词，支持设置生效时间</p>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索关键词" clearable @keyup.enter="handleSearch" />
      <el-select v-model="query.status" placeholder="全部状态" clearable @change="handleSearch">
        <el-option v-for="item in ENABLED_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      <div class="search-spacer" />
      <el-button type="primary" :icon="Plus" @click="handleAdd">新建热词</el-button>
    </div>

    <div class="page-card">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="关键词" prop="keyword" min-width="160" />
        <el-table-column label="显示名" prop="displayName" width="160">
          <template #default="{ row }">{{ row.displayName || '-' }}</template>
        </el-table-column>
        <el-table-column label="图标" prop="icon" width="120" align="center">
          <template #default="{ row }">{{ row.icon || '-' }}</template>
        </el-table-column>
        <el-table-column label="排序" prop="sortNo" width="80" align="center" />
        <el-table-column label="点击数" prop="clickCount" width="100" align="center" />
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
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row as AdminHotKeywordVO)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row as AdminHotKeywordVO)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑热词' : '新建热词'" width="520px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="editing" :rules="rules" label-width="90px">
        <el-form-item label="关键词" prop="keyword">
          <el-input v-model="editing.keyword" placeholder="请输入关键词" />
        </el-form-item>
        <el-form-item label="显示名">
          <el-input v-model="editing.displayName" placeholder="请输入显示名（可选）" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="editing.icon" placeholder="请输入图标标识（可选）" />
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
import { listHotKeywords, createHotKeyword, updateHotKeyword, deleteHotKeyword, type HotKeywordListQuery } from '@/api/hotKeyword'
import { ENABLED_STATUS_OPTIONS, getEnabledStatusLabel, getEnabledStatusTagType } from '@/constants/enums'
import type { AdminHotKeywordVO, AdminHotKeywordSaveRequest } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const list = ref<AdminHotKeywordVO[]>([])
const total = ref(0)

const query = reactive<HotKeywordListQuery>({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const editing = reactive<AdminHotKeywordSaveRequest>({
  id: undefined,
  keyword: '',
  displayName: '',
  icon: '',
  sortNo: 0,
  startAt: '',
  endAt: '',
  status: 1
})

const rules: FormRules = {
  keyword: [{ required: true, message: '请输入关键词', trigger: 'blur' }]
}

async function loadList() {
  loading.value = true
  try {
    const res = await listHotKeywords(query)
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

function resetEditing() {
  editing.id = undefined
  editing.keyword = ''
  editing.displayName = ''
  editing.icon = ''
  editing.sortNo = 0
  editing.startAt = ''
  editing.endAt = ''
  editing.status = 1
}

function handleAdd() {
  resetEditing()
  dialogVisible.value = true
}

function handleEdit(row: AdminHotKeywordVO) {
  resetEditing()
  editing.id = row.id
  editing.keyword = row.keyword
  editing.displayName = row.displayName || ''
  editing.icon = row.icon || ''
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
      const payload: AdminHotKeywordSaveRequest = { ...editing }
      if (editing.id) {
        await updateHotKeyword(editing.id, payload)
        ElMessage.success('更新成功')
      } else {
        await createHotKeyword(payload)
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

async function handleDelete(row: AdminHotKeywordVO) {
  try {
    await ElMessageBox.confirm(`确定要删除热词「${row.keyword}」吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }
  try {
    await deleteHotKeyword(row.id)
    ElMessage.success('删除成功')
    if (list.value.length === 1 && query.page! > 1) {
      query.page = query.page! - 1
    }
    loadList()
  } catch {
    // ignore
  }
}

onMounted(loadList)
</script>

<style scoped>
.hot-keyword-list-page {
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
