<template>
  <div class="tag-list-page">
    <div class="page-header">
      <h2>标签管理</h2>
      <p>维护平台标签，支持启用/停用</p>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索名称/编码" clearable @keyup.enter="handleSearch" />
      <el-select v-model="query.status" placeholder="全部状态" clearable @change="handleSearch">
        <el-option v-for="item in ENABLED_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      <div class="search-spacer" />
      <el-button type="primary" :icon="Plus" @click="handleAdd">新建标签</el-button>
    </div>

    <div class="page-card">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="标签名" prop="tagName" min-width="160" />
        <el-table-column label="编码" prop="tagCode" width="180" />
        <el-table-column label="使用次数" prop="useCount" width="110" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getEnabledStatusTagType(row.status)" size="small">{{ getEnabledStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt" width="180" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row as AdminTagVO)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row as AdminTagVO)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑标签' : '新建标签'" width="480px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="editing" :rules="rules" label-width="80px">
        <el-form-item label="标签名" prop="tagName">
          <el-input v-model="editing.tagName" placeholder="请输入标签名" />
        </el-form-item>
        <el-form-item label="编码" prop="tagCode">
          <el-input v-model="editing.tagCode" placeholder="请输入标签编码" />
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
import { listTags, createTag, updateTag, deleteTag, type TagListQuery } from '@/api/tag'
import { ENABLED_STATUS_OPTIONS, getEnabledStatusLabel, getEnabledStatusTagType } from '@/constants/enums'
import type { AdminTagVO, AdminTagSaveRequest } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const list = ref<AdminTagVO[]>([])
const total = ref(0)

const query = reactive<TagListQuery>({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const editing = reactive<AdminTagSaveRequest>({
  id: undefined,
  tagName: '',
  tagCode: '',
  status: 1
})

const rules: FormRules = {
  tagName: [{ required: true, message: '请输入标签名', trigger: 'blur' }],
  tagCode: [{ required: true, message: '请输入标签编码', trigger: 'blur' }]
}

async function loadList() {
  loading.value = true
  try {
    const res = await listTags(query)
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
  editing.tagName = ''
  editing.tagCode = ''
  editing.status = 1
}

function handleAdd() {
  resetEditing()
  dialogVisible.value = true
}

function handleEdit(row: AdminTagVO) {
  resetEditing()
  editing.id = row.id
  editing.tagName = row.tagName
  editing.tagCode = row.tagCode
  editing.status = row.status ?? 1
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: AdminTagSaveRequest = { ...editing }
      if (editing.id) {
        await updateTag(editing.id, payload)
        ElMessage.success('更新成功')
      } else {
        await createTag(payload)
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

async function handleDelete(row: AdminTagVO) {
  try {
    await ElMessageBox.confirm(`确定要删除标签「${row.tagName}」吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }
  try {
    await deleteTag(row.id)
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
.tag-list-page {
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
