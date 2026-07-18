<template>
  <div class="category-list-page">
    <div class="page-header">
      <h2>分类管理</h2>
      <p>维护平台内容分类，支持启用/停用与排序</p>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索名称/编码" clearable @keyup.enter="handleSearch" />
      <el-select v-model="query.status" placeholder="全部状态" clearable @change="handleSearch">
        <el-option v-for="item in ENABLED_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      <div class="search-spacer" />
      <el-button type="primary" :icon="Plus" @click="handleAdd">新建分类</el-button>
    </div>

    <div class="page-card">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="编码" prop="categoryCode" width="140" />
        <el-table-column label="名称" prop="categoryName" width="160" />
        <el-table-column label="图标" prop="icon" width="120" align="center">
          <template #default="{ row }">{{ row.icon || '-' }}</template>
        </el-table-column>
        <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="排序" prop="sortNo" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getEnabledStatusTagType(row.status)" size="small">{{ getEnabledStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row as AdminCategoryVO)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row as AdminCategoryVO)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑分类' : '新建分类'" width="520px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="editing" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="categoryCode">
          <el-input v-model="editing.categoryCode" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="名称" prop="categoryName">
          <el-input v-model="editing.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="editing.icon" placeholder="请输入图标标识（可选）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editing.description" type="textarea" :rows="3" placeholder="请输入分类描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editing.sortNo" :min="0" :max="9999" style="width: 100%" />
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
import { listCategories, createCategory, updateCategory, deleteCategory, type CategoryListQuery } from '@/api/category'
import { ENABLED_STATUS_OPTIONS, getEnabledStatusLabel, getEnabledStatusTagType } from '@/constants/enums'
import type { AdminCategoryVO, AdminCategorySaveRequest } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const list = ref<AdminCategoryVO[]>([])
const total = ref(0)

const query = reactive<CategoryListQuery>({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const editing = reactive<AdminCategorySaveRequest>({
  id: undefined,
  parentCode: '',
  categoryCode: '',
  categoryName: '',
  icon: '',
  description: '',
  sortNo: 0,
  status: 1
})

const rules: FormRules = {
  categoryCode: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

async function loadList() {
  loading.value = true
  try {
    const res = await listCategories(query)
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
  editing.parentCode = ''
  editing.categoryCode = ''
  editing.categoryName = ''
  editing.icon = ''
  editing.description = ''
  editing.sortNo = 0
  editing.status = 1
}

function handleAdd() {
  resetEditing()
  dialogVisible.value = true
}

function handleEdit(row: AdminCategoryVO) {
  resetEditing()
  editing.id = row.id
  editing.parentCode = row.parentId ? String(row.parentId) : ''
  editing.categoryCode = row.categoryCode
  editing.categoryName = row.categoryName
  editing.icon = row.icon || ''
  editing.description = row.description || ''
  editing.sortNo = row.sortNo ?? 0
  editing.status = row.status ?? 1
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: AdminCategorySaveRequest = { ...editing }
      if (editing.id) {
        await updateCategory(editing.id, payload)
        ElMessage.success('更新成功')
      } else {
        await createCategory(payload)
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

async function handleDelete(row: AdminCategoryVO) {
  try {
    await ElMessageBox.confirm(`确定要删除分类「${row.categoryName}」吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }
  try {
    await deleteCategory(row.id)
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
.category-list-page {
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
