<template>
  <div class="focus-page">
    <div class="timer-card">
      <div class="timer-display">{{ formattedTime }}</div>
      <div class="timer-status">{{ statusText }}</div>

      <div class="settings">
        <el-select v-model="duration" placeholder="时长" :disabled="isRunning">
          <el-option v-for="m in [15, 25, 45, 60, 90]" :key="m" :label="m + ' 分钟'" :value="m" />
        </el-select>
        <el-select v-model="subjectId" placeholder="选择科目" clearable :disabled="isRunning">
          <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
        <el-input
          v-model="note"
          placeholder="备注，如：背单词、刷高数题"
          :disabled="isRunning"
          style="width: 220px"
        />
      </div>

      <div class="actions">
        <el-button v-if="!isRunning && !isPaused" type="primary" size="large" @click="start">开始专注</el-button>
        <el-button v-if="isRunning" type="warning" size="large" @click="pause">暂停</el-button>
        <el-button v-if="isPaused" type="primary" size="large" @click="resume">继续</el-button>
        <el-button v-if="isRunning || isPaused" type="danger" size="large" @click="stop">结束专注</el-button>
      </div>
    </div>

    <el-card v-if="sessions.length > 0" class="session-list">
      <template #header>今日专注记录</template>
      <el-timeline>
        <el-timeline-item v-for="s in sessions" :key="s.id">
          {{ formatTime(s.startTime) }} - {{ formatTime(s.endTime) }}
          <el-tag size="small" type="info">{{ s.subjectName || '未分类' }}</el-tag>
          {{ s.durationMinutes }} 分钟
          <span v-if="s.note" class="session-note">{{ s.note }}</span>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { startFocus, stopFocus, getFocusSessions } from '../api/focus.js'
import { getSubjects } from '../api/card.js'

const route = useRoute()
const router = useRouter()

const duration = ref(route.query.duration ? Number(route.query.duration) : 25)
const subjectId = ref(route.query.subjectId ? Number(route.query.subjectId) : null)
const note = ref(route.query.title || '')

const subjects = ref([])
const sessions = ref([])
const sessionId = ref(null)
const remainingSeconds = ref(duration.value * 60)
const isRunning = ref(false)
const isPaused = ref(false)
let timer = null

const formattedTime = computed(() => {
  const m = Math.floor(remainingSeconds.value / 60).toString().padStart(2, '0')
  const s = (remainingSeconds.value % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})

const statusText = computed(() => {
  if (isRunning.value) return '专注中...'
  if (isPaused.value) return '已暂停'
  return '准备开始'
})

const start = async () => {
  try {
    const res = await startFocus({
      subjectId: subjectId.value,
      note: note.value
    })
    sessionId.value = res.data.id
    remainingSeconds.value = duration.value * 60
    isRunning.value = true
    isPaused.value = false
    startTimer()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--
    } else {
      stop()
    }
  }, 1000)
}

const pause = () => {
  isRunning.value = false
  isPaused.value = true
  clearInterval(timer)
}

const resume = () => {
  isRunning.value = true
  isPaused.value = false
  startTimer()
}

const stop = async () => {
  clearInterval(timer)
  if (!sessionId.value) return
  try {
    await stopFocus(sessionId.value)
    ElMessage.success('专注结束')
    isRunning.value = false
    isPaused.value = false
    sessionId.value = null
    fetchSessions()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const fetchSessions = async () => {
  try {
    const res = await getFocusSessions()
    sessions.value = res.data || []
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(async () => {
  const res = await getSubjects()
  subjects.value = res.data || []
  fetchSessions()
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
.focus-page {
  max-width: 800px;
  margin: 0 auto;
}
.timer-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  margin-bottom: 20px;
}
.timer-display {
  font-size: 96px;
  font-weight: bold;
  color: #409eff;
  font-variant-numeric: tabular-nums;
  letter-spacing: 4px;
}
.timer-status {
  color: #909399;
  margin: 12px 0 24px;
  font-size: 16px;
}
.settings {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}
.session-list {
  padding: 10px;
}
.session-note {
  color: #909399;
  margin-left: 8px;
}
</style>
