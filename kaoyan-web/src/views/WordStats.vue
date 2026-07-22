<template>
  <div class="stats-page">
    <h2>学习报告</h2>

    <div class="summary">
      <el-card class="summary-card xp-card">
        <div class="summary-label">总 XP</div>
        <div class="summary-value">{{ stats.totalXp }}</div>
        <div class="summary-sub">Lv.{{ stats.level }}</div>
      </el-card>
      <el-card class="summary-card">
        <div class="summary-label">连续打卡</div>
        <div class="summary-value">🔥 {{ stats.streakDays }} 天</div>
      </el-card>
      <el-card class="summary-card">
        <div class="summary-label">总单词</div>
        <div class="summary-value">{{ stats.total }}</div>
        <div class="summary-sub">必背 {{ stats.mustCount }} / 主考 {{ stats.examCount }}</div>
      </el-card>
      <el-card class="summary-card">
        <div class="summary-label">今日待复习</div>
        <div class="summary-value">{{ stats.today }}</div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>近 7 天复习量</template>
          <div ref="barChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>掌握度分布</template>
          <div ref="pieChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getWordStats, getWordWeekly } from '../api/word.js'

const stats = ref({})
const barChartRef = ref(null)
const pieChartRef = ref(null)
let barChart = null
let pieChart = null

const stageColors = ['#f56c6c', '#f78c6c', '#e6a23c', '#e6c93c', '#c0c43c', '#a0c23c', '#67c23a', '#3c9e3a']

const loadStats = async () => {
  const res = await getWordStats()
  stats.value = res.data
  renderPie()
}

const loadWeekly = async () => {
  const res = await getWordWeekly()
  renderBar(res.data.daily)
}

const renderBar = (daily) => {
  if (!barChart) return
  const dates = Object.keys(daily)
  const counts = dates.map(d => daily[d].count)
  const corrects = dates.map(d => daily[d].correct)
  barChart.setOption({
    tooltip: {},
    legend: { data: ['复习数', '掌握数'] },
    xAxis: { type: 'category', data: dates.map(d => d.slice(5)) },
    yAxis: { type: 'value' },
    series: [
      { name: '复习数', type: 'bar', data: counts, itemStyle: { color: '#409eff' } },
      { name: '掌握数', type: 'bar', data: corrects, itemStyle: { color: '#67c23a' } }
    ]
  })
}

const renderPie = () => {
  if (!pieChart) return
  const dist = stats.value.stageDistribution || {}
  const data = Object.entries(dist).map(([stage, count]) => ({
    name: `阶段 ${stage}`,
    value: count,
    itemStyle: { color: stageColors[stage] || '#909399' }
  }))
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data,
      label: { show: true, formatter: '{b}\n{c} 词' }
    }]
  })
}

onMounted(() => {
  barChart = echarts.init(barChartRef.value)
  pieChart = echarts.init(pieChartRef.value)
  loadStats()
  loadWeekly()
  window.addEventListener('resize', () => {
    barChart?.resize()
    pieChart?.resize()
  })
})

onUnmounted(() => {
  barChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.stats-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
.summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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
  font-size: 28px;
  font-weight: bold;
  margin: 8px 0;
  color: #409eff;
}
.summary-sub {
  color: #909399;
  font-size: 13px;
}
.xp-card .summary-value {
  color: #ffd700;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.4);
}
.chart {
  height: 280px;
}
</style>
