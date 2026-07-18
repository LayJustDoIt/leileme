<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2>数据概览</h2>
      <p>实时查看平台运营关键指标</p>
    </div>

    <div v-loading="loading" class="dashboard-content">
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="8" v-for="card in cards" :key="card.key">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-inner">
              <div class="stat-icon" :style="{ background: card.color }">
                <el-icon :size="22"><component :is="card.icon" /></el-icon>
              </div>
              <div class="stat-meta">
                <el-statistic :title="card.title" :value="card.value" />
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, markRaw } from 'vue'
import { Document, Promotion, Edit, Files, PriceTag, ChatDotRound, ChatLineSquare, Plus, Message } from '@element-plus/icons-vue'
import { getDashboardSummary } from '@/api/dashboard'
import type { DashboardSummaryVO } from '@/types'

const loading = ref(false)
const summary = ref<DashboardSummaryVO>({
  contentTotal: 0,
  contentPublished: 0,
  contentDraft: 0,
  categoryTotal: 0,
  tagTotal: 0,
  feedbackPending: 0,
  feedbackTotal: 0,
  todayContentCount: 0,
  todayFeedbackCount: 0
})

const cards = computed(() => [
  { key: 'contentTotal', title: '内容总数', value: summary.value.contentTotal, icon: markRaw(Document), color: '#409EFF' },
  { key: 'contentPublished', title: '已发布内容', value: summary.value.contentPublished, icon: markRaw(Promotion), color: '#67C23A' },
  { key: 'contentDraft', title: '草稿数量', value: summary.value.contentDraft, icon: markRaw(Edit), color: '#909399' },
  { key: 'categoryTotal', title: '分类总数', value: summary.value.categoryTotal, icon: markRaw(Files), color: '#E6A23C' },
  { key: 'tagTotal', title: '标签总数', value: summary.value.tagTotal, icon: markRaw(PriceTag), color: '#9C27B0' },
  { key: 'feedbackPending', title: '待处理反馈', value: summary.value.feedbackPending, icon: markRaw(ChatDotRound), color: '#F56C6C' },
  { key: 'feedbackTotal', title: '反馈总数', value: summary.value.feedbackTotal, icon: markRaw(ChatLineSquare), color: '#00BCD4' },
  { key: 'todayContentCount', title: '今日新增内容', value: summary.value.todayContentCount, icon: markRaw(Plus), color: '#3F51B5' },
  { key: 'todayFeedbackCount', title: '今日新增反馈', value: summary.value.todayFeedbackCount, icon: markRaw(Message), color: '#FF9800' }
])

async function loadSummary() {
  loading.value = true
  try {
    const data = await getDashboardSummary()
    summary.value = data
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(loadSummary)
</script>

<style scoped>
.dashboard-page {
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

.stat-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-meta {
  flex: 1;
  min-width: 0;
}
</style>
