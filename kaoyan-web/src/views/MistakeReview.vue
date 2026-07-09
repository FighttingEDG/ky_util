<template>
  <div class="review-page">
    <div class="header">
      <h2>错题重做</h2>
      <el-tag type="warning" size="large">剩余 {{ queue.length }} 道</el-tag>
    </div>

    <div v-if="loading" class="center">加载中...</div>
    <div v-else-if="queue.length === 0" class="center">
      <el-result icon="success" title="今日错题复习已完成" sub-title="明天再来吧"></el-result>
    </div>
    <div v-else class="question-box">
      <el-card class="question-card">
        <template #header>
          <div class="card-header">
            <span>{{ current.subjectName }}</span>
            <el-tag v-if="current.wrongReason" size="small">{{ current.wrongReason }}</el-tag>
          </div>
        </template>
        <p class="label">题目</p>
        <p class="content">{{ current.question }}</p>
      </el-card>

      <el-card v-if="showAnswer" class="answer-card">
        <p class="label">正确答案</p>
        <p class="content">{{ current.correctAnswer }}</p>
        <div v-if="current.wrongAnswer">
          <p class="label">你当时的答案</p>
          <p class="wrong-answer">{{ current.wrongAnswer }}</p>
        </div>
        <div v-if="current.analysis">
          <p class="label">解析</p>
          <p class="content">{{ current.analysis }}</p>
        </div>
      </el-card>

      <div class="actions">
        <el-button v-if="!showAnswer" type="primary" @click="showAnswer = true">查看答案</el-button>
        <template v-else>
          <el-button type="danger" @click="review('again')">Again（未懂）</el-button>
          <el-button type="warning" @click="review('hard')">Hard（困难）</el-button>
          <el-button type="primary" @click="review('good')">Good（掌握）</el-button>
          <el-button type="success" @click="review('easy')">Easy（熟练）</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayMistakes, reviewMistake } from '../api/mistake.js'

const queue = ref([])
const currentIndex = ref(0)
const showAnswer = ref(false)
const loading = ref(false)

const current = computed(() => queue.value[currentIndex.value])

const load = async () => {
  loading.value = true
  try {
    const res = await getTodayMistakes()
    queue.value = res.data || []
    currentIndex.value = 0
    showAnswer.value = false
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const review = async (result) => {
  try {
    await reviewMistake(current.value.id, result)
    queue.value.splice(currentIndex.value, 1)
    showAnswer.value = false
    if (currentIndex.value >= queue.value.length) {
      currentIndex.value = 0
    }
    ElMessage.success('已记录')
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(load)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.center {
  margin-top: 80px;
  text-align: center;
  color: #909399;
}
.question-box {
  max-width: 800px;
  margin: 0 auto;
}
.question-card, .answer-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}
.content {
  font-size: 18px;
  line-height: 1.6;
  white-space: pre-wrap;
  margin-bottom: 16px;
}
.wrong-answer {
  font-size: 16px;
  color: #f56c6c;
  text-decoration: line-through;
  margin-bottom: 16px;
}
.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
