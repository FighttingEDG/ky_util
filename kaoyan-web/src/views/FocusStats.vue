<template>
  <div class="stats-page">
    <h2>专注统计</h2>

    <div class="summary">
      <el-card v-for="(value, key) in summary" :key="key" class="summary-card">
        <div class="summary-label">{{ summaryLabels[key] }}</div>
        <div class="summary-value">{{ formatMinutes(value) }}</div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>今日科目分布</template>
          <el-empty v-if="todayBySubject.length === 0" description="暂无数据" />
          <div v-else class="subject-list">
            <div v-for="item in todayBySubject" :key="item.subjectName" class="subject-item">
              <span>{{ item.subjectName || '未分类' }}</span>
              <div class="progress">
                <div class="progress-bar" :style="{ width: subjectPercent(item.totalMinutes) + '%' }"></div>
              </div>
              <span class="minutes">{{ formatMinutes(item.totalMinutes) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>近 7 天专注时长</template>
          <div class="day-list">
            <div v-for="(minutes, date) in last7Days" :key="date" class="day-item">
              <span class="day-date">{{ date.slice(5) }}</span>
              <div class="progress">
                <div class="progress-bar" :style="{ width: dayPercent(minutes) + '%' }"></div>
              </div>
              <span class="minutes">{{ formatMinutes(minutes) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getFocusStats } from '../api/focus.js'

const stats = ref({
  today: 0,
  week: 0,
  month: 0,
  todayBySubject: [],
  last7Days: {}
})

const summary = computed(() => ({
  today: stats.value.today,
  week: stats.value.week,
  month: stats.value.month
}))

const summaryLabels = {
  today: '今日专注',
  week: '本周专注',
  month: '本月专注'
}

const todayBySubject = computed(() => stats.value.todayBySubject || [])
const last7Days = computed(() => stats.value.last7Days || {})

const maxSubjectMinutes = computed(() => {
  const list = todayBySubject.value
  if (list.length === 0) return 1
  return Math.max(...list.map(i => Number(i.totalMinutes) || 0))
})

const maxDayMinutes = computed(() => {
  const values = Object.values(last7Days.value).map(Number)
  if (values.length === 0) return 1
  return Math.max(...values)
})

const formatMinutes = (m) => {
  const minutes = Number(m) || 0
  const h = Math.floor(minutes / 60)
  const min = minutes % 60
  if (h > 0) return `${h}小时${min}分`
  return `${min}分钟`
}

const subjectPercent = (m) => {
  return Math.round((Number(m) || 0) / maxSubjectMinutes.value * 100)
}

const dayPercent = (m) => {
  return Math.round((Number(m) || 0) / maxDayMinutes.value * 100)
}

const fetch = async () => {
  try {
    const res = await getFocusStats()
    stats.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(fetch)
</script>

<style scoped>
.stats-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
.summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.summary-card {
  text-align: center;
}
.summary-label {
  color: #909399;
  font-size: 14px;
}
.summary-value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 8px;
  color: #409eff;
}
.subject-list, .day-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.subject-item, .day-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.subject-item span:first-child, .day-date {
  width: 80px;
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}
.progress {
  flex: 1;
  height: 10px;
  background: #ebeef5;
  border-radius: 5px;
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: #409eff;
  border-radius: 5px;
  transition: width 0.3s;
}
.minutes {
  width: 70px;
  text-align: right;
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}
</style>
