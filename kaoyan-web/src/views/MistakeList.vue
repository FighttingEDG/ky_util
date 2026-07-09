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
      <el-select v-model="query.wrongReason" placeholder="错误原因" clearable @change="fetch">
        <el-option
          v-for="r in reasons"
          :key="r"
          :label="r"
          :value="r"
        />
      </el-select>
      <el-input
        v-model="query.tag"
        placeholder="标签搜索"
        clearable
        style="width: 160px"
        @change="fetch"
      />
      <el-input
        v-model="query.keyword"
        placeholder="关键词搜索"
        clearable
        style="width: 200px"
        @change="fetch"
      />
      <el-button type="primary" @click="$router.push('/mistakes/new')">+ 新增错题</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="18">
        <el-table :data="mistakes" v-loading="loading" stripe>
          <el-table-column prop="subjectName" label="科目" width="90" />
          <el-table-column label="题目" min-width="200">
            <template #default="{ row }">
              <div class="ellipsis">{{ row.question }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="wrongReason" label="错误原因" width="110" />
          <el-table-column prop="tags" label="知识点" width="140" />
          <el-table-column prop="stage" label="阶段" width="70" />
          <el-table-column label="下次复习" width="110">
            <template #default="{ row }">
              {{ formatDate(row.nextReviewDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" @click="$router.push(`/mistakes/edit/${row.id}`)">编辑</el-button>
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
      </el-col>

      <el-col :span="6">
        <el-card>
          <template #header>薄弱知识点 Top 10</template>
          <el-empty v-if="weakPoints.length === 0" description="暂无数据" />
          <div v-else class="weak-list">
            <div v-for="(item, index) in weakPoints" :key="index" class="weak-item">
              <span class="weak-tag">{{ item.tag }}</span>
              <el-tag type="danger" size="small">{{ item.count }} 次</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMistakes, getMistakeStats, deleteMistake, getWeakPoints } from '../api/mistake.js'
import { getSubjects } from '../api/card.js'

const subjects = ref([])
const mistakes = ref([])
const total = ref(0)
const loading = ref(false)
const stats = ref({})
const weakPoints = ref([])

const reasons = ['概念不清', '计算失误', '审题不清', '公式记错', '其他']

const statLabels = {
  total: '总错题',
  today: '今日待复习',
  mastered: '已掌握',
  learning: '学习中'
}

const query = reactive({
  subjectId: null,
  wrongReason: '',
  tag: '',
  keyword: '',
  page: 1,
  size: 10
})

const formatDate = (date) => {
  if (!date || date === '9999-12-31') return '-'
  return date
}

const fetch = async () => {
  loading.value = true
  try {
    const res = await getMistakes({
      subjectId: query.subjectId,
      wrongReason: query.wrongReason,
      tag: query.tag,
      keyword: query.keyword,
      page: query.page - 1,
      size: query.size
    })
    mistakes.value = res.data.content
    total.value = res.data.totalElements
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  const res = await getMistakeStats()
  stats.value = res.data
}

const fetchWeakPoints = async () => {
  const res = await getWeakPoints(10)
  weakPoints.value = res.data || []
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这道错题吗？', '提示', { type: 'warning' })
    await deleteMistake(id)
    ElMessage.success('删除成功')
    fetch()
    fetchStats()
    fetchWeakPoints()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message)
    }
  }
}

watch(() => [query.subjectId, query.wrongReason, query.tag, query.keyword], () => {
  query.page = 1
  fetch()
}, { deep: true })

onMounted(async () => {
  const res = await getSubjects()
  subjects.value = res.data || []
  fetch()
  fetchStats()
  fetchWeakPoints()
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
  flex-wrap: wrap;
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
.weak-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.weak-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
.weak-tag {
  color: #606266;
  font-size: 14px;
}
</style>
