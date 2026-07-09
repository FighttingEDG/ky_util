<template>
  <div class="review-page">
    <div class="header">
      <h2>今日复习</h2>
      <el-tag type="warning" size="large">剩余 {{ queue.length }} 张</el-tag>
    </div>

    <div v-if="loading" class="center">加载中...</div>
    <div v-else-if="queue.length === 0" class="center">
      <el-result icon="success" title="今日复习已完成" sub-title="明天再来吧"></el-result>
    </div>
    <div v-else class="card-box">
      <div
        class="card"
        :class="{ flipped: showBack }"
        @click="showBack = !showBack"
      >
        <div class="card-face card-front">
          <p class="label">正面</p>
          <p class="content">{{ current.front }}</p>
          <p class="hint">点击卡片查看背面</p>
        </div>
        <div class="card-face card-back">
          <p class="label">背面</p>
          <p class="content">{{ current.back }}</p>
        </div>
      </div>

      <div v-if="showBack" class="actions">
        <el-button type="danger" @click="review('again')">Again（忘记）</el-button>
        <el-button type="warning" @click="review('hard')">Hard（困难）</el-button>
        <el-button type="primary" @click="review('good')">Good（正常）</el-button>
        <el-button type="success" @click="review('easy')">Easy（简单）</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayCards, reviewCard } from '../api/card.js'

const queue = ref([])
const currentIndex = ref(0)
const showBack = ref(false)
const loading = ref(false)

const current = computed(() => queue.value[currentIndex.value])

const load = async () => {
  loading.value = true
  try {
    const res = await getTodayCards()
    queue.value = res.data || []
    currentIndex.value = 0
    showBack.value = false
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const review = async (result) => {
  try {
    await reviewCard(current.value.id, result)
    queue.value.splice(currentIndex.value, 1)
    showBack.value = false
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
.card-box {
  perspective: 1000px;
}
.card {
  width: 100%;
  max-width: 600px;
  margin: 0 auto 30px;
  min-height: 320px;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.5s;
  cursor: pointer;
}
.card.flipped {
  transform: rotateY(180deg);
}
.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 30px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.card-back {
  transform: rotateY(180deg);
}
.label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}
.content {
  font-size: 22px;
  line-height: 1.6;
  flex: 1;
  white-space: pre-wrap;
}
.hint {
  color: #c0c4cc;
  font-size: 13px;
  text-align: center;
}
.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
