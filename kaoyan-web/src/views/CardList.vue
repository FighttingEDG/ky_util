<template>
  <div class="list-page">
    <div class="stats">
      <el-card v-for="(value, key) in stats" :key="key" class="stat-card">
        <div class="stat-label">{{ statLabels[key] }}</div>
        <div class="stat-value">{{ value }}</div>
      </el-card>
    </div>

    <div class="toolbar">
      <el-select v-model="query.subjectId" placeholder="全部科目" clearable @change="fetch">
        <el-option
          v-for="s in subjects"
          :key="s.id"
          :label="s.name"
          :value="s.id"
        />
      </el-select>
      <el-input
        v-model="query.tag"
        placeholder="标签搜索"
        clearable
        style="width: 180px"
        @change="fetch"
      />
      <el-input
        v-model="query.keyword"
        placeholder="关键词搜索"
        clearable
        style="width: 220px"
        @change="fetch"
      />
      <el-button type="primary" @click="$router.push('/cards/new')">+ 新增卡片</el-button>
    </div>

    <el-table :data="cards" v-loading="loading" stripe>
      <el-table-column prop="subjectName" label="科目" width="100" />
      <el-table-column label="正面" min-width="200">
        <template #default="{ row }">
          <div class="ellipsis">{{ row.front }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="tags" label="标签" width="150" />
      <el-table-column prop="stage" label="阶段" width="80" />
      <el-table-column label="下次复习" width="120">
        <template #default="{ row }">
          {{ formatDate(row.nextReviewDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/cards/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetch"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCards, getStats, getSubjects, deleteCard } from '../api/card.js'

const subjects = ref([])
const cards = ref([])
const total = ref(0)
const loading = ref(false)
const stats = ref({})

const statLabels = {
  total: '总卡片',
  today: '今日待复习',
  mastered: '已掌握',
  learning: '学习中'
}

const query = reactive({
  subjectId: null,
  tag: '',
  keyword: '',
  page: 1,
  size: 10
})

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const fetch = async () => {
  loading.value = true
  try {
    const res = await getCards({
      subjectId: query.subjectId,
      tag: query.tag,
      keyword: query.keyword,
      page: query.page - 1,
      size: query.size
    })
    cards.value = res.data.content
    total.value = res.data.totalElements
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  const res = await getStats()
  stats.value = res.data
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这张卡片吗？', '提示', { type: 'warning' })
    await deleteCard(id)
    ElMessage.success('删除成功')
    fetch()
    fetchStats()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message)
    }
  }
}

watch(() => [query.subjectId, query.tag, query.keyword], () => {
  query.page = 1
  fetch()
}, { deep: true })

onMounted(async () => {
  const res = await getSubjects()
  subjects.value = res.data || []
  fetch()
  fetchStats()
})
</script>

<style scoped>
.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
}
.stat-label {
  color: #909399;
  font-size: 14px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-top: 8px;
  color: #409eff;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
}
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.list-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
</style>
