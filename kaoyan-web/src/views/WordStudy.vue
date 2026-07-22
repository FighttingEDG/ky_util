<template>
  <div class="study-page">
    <div class="top-bar">
      <div class="progress-info">
        <el-progress :percentage="progressPercent" :show-text="false" :stroke-width="8" color="#409eff" />
        <span class="progress-text">{{ currentIndex + 1 }} / {{ queue.length }}</span>
      </div>
      <div class="stats-info">
        <span class="stat-chip xp">XP {{ totalXp }}</span>
        <span class="stat-chip streak">🔥 {{ streakDays }} 天</span>
        <el-dropdown trigger="click" class="voice-setting">
          <span class="stat-chip voice">🎙️ 语速 {{ rate.toFixed(1) }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="r in [0.6, 0.7, 0.8, 0.85, 0.9, 1.0, 1.2]" :key="r" @click="rate = r">
                {{ r.toFixed(1) }} {{ r === rate ? '✓' : '' }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div v-if="loading" class="center">加载中...</div>
    <div v-else-if="queue.length === 0" class="center">
      <div class="done-box">
        <div class="done-icon">🎉</div>
        <h2>今日学习完成！</h2>
        <p>明天继续加油</p>
        <el-button type="primary" @click="$router.push('/words/list')">查看单词库</el-button>
      </div>
    </div>
    <div v-else class="card-area" @click="flipped = !flipped">
      <div class="card" :class="{ flipped }">
        <div class="card-face card-front">
          <div class="word-main">{{ current.word }}</div>
          <div class="phonetic" @click.stop="speak(current.word)">
            {{ current.phonetic }}
            <el-icon class="sound-icon"><VideoPlay /></el-icon>
          </div>
          <div class="hint">点击卡片查看释义</div>
        </div>
        <div class="card-face card-back">
          <div class="word-class">{{ current.wordClass }}</div>
          <div class="meaning">{{ current.meaning }}</div>
          <div v-if="current.examples?.length" class="section">
            <div class="section-title">考点例句</div>
            <div v-for="(ex, i) in current.examples.slice(0, 3)" :key="i" class="example">{{ ex }}</div>
          </div>
          <div v-if="current.phrases?.length" class="section">
            <div class="section-title">词组</div>
            <div v-for="(p, i) in current.phrases.slice(0, 3)" :key="i" class="phrase">{{ p }}</div>
          </div>
          <div v-if="current.rootMemo" class="section">
            <div class="section-title">词根记忆</div>
            <div class="example">{{ current.rootMemo }}</div>
          </div>
          <div v-if="current.derivatives" class="section">
            <div class="section-title">派生词</div>
            <div class="phrase">{{ current.derivatives }}</div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="queue.length > 0 && flipped" class="actions">
      <button class="rate-btn again" @click.stop="review('again')">
        <span class="rate-label">Again</span>
        <span class="rate-xp">+5 XP</span>
        <span class="rate-key">1</span>
      </button>
      <button class="rate-btn hard" @click.stop="review('hard')">
        <span class="rate-label">Hard</span>
        <span class="rate-xp">+10 XP</span>
        <span class="rate-key">2</span>
      </button>
      <button class="rate-btn good" @click.stop="review('good')">
        <span class="rate-label">Good</span>
        <span class="rate-xp">+15 XP</span>
        <span class="rate-key">3</span>
      </button>
      <button class="rate-btn easy" @click.stop="review('easy')">
        <span class="rate-label">Easy</span>
        <span class="rate-xp">+20 XP</span>
        <span class="rate-key">4</span>
      </button>
    </div>

    <transition name="xp-float">
      <div v-if="xpFloat.show" class="xp-float" :style="{ left: xpFloat.x + 'px', top: xpFloat.y + 'px' }">
        +{{ xpFloat.amount }} XP
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'
import { getStudyQueue, reviewWord } from '../api/word.js'

const queue = ref([])
const currentIndex = ref(0)
const flipped = ref(false)
const loading = ref(false)
const totalXp = ref(0)
const streakDays = ref(0)
const rate = ref(0.85)
const xpFloat = ref({ show: false, amount: 0, x: 0, y: 0 })

const current = computed(() => queue.value[currentIndex.value] || {})
const progressPercent = computed(() => {
  if (queue.value.length === 0) return 100
  return Math.round(((currentIndex.value + 1) / queue.value.length) * 100)
})

const speak = (text) => {
  if (!text) return
  speechSynthesis.cancel()
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  utterance.rate = rate.value
  utterance.pitch = 1.0

  const voices = speechSynthesis.getVoices().filter(v => v.lang.startsWith('en'))
  const preferred = voices.find(v =>
    v.name.includes('Samantha') || v.name.includes('Alex') ||
    v.name.includes('Google US English') || v.name.includes('Microsoft')
  )
  if (preferred) utterance.voice = preferred

  speechSynthesis.speak(utterance)
}

const load = async () => {
  loading.value = true
  try {
    const res = await getStudyQueue()
    queue.value = res.data || []
    currentIndex.value = 0
    flipped.value = false
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const review = async (result, event) => {
  try {
    const res = await reviewWord(current.value.id, result)
    const data = res.data
    totalXp.value = data.totalXp
    streakDays.value = data.streakDays

    xpFloat.value = {
      show: true,
      amount: data.xp,
      x: event?.clientX || window.innerWidth / 2,
      y: (event?.clientY || window.innerHeight / 2) - 40
    }
    setTimeout(() => { xpFloat.value.show = false }, 800)

    queue.value.splice(currentIndex.value, 1)
    flipped.value = false
    if (currentIndex.value >= queue.value.length) {
      currentIndex.value = 0
    }
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const handleKey = (e) => {
  if (queue.value.length === 0) return
  if (e.code === 'Space') {
    e.preventDefault()
    flipped.value = !flipped.value
    return
  }
  if (!flipped.value) return
  const map = { Digit1: 'again', Digit2: 'hard', Digit3: 'good', Digit4: 'easy' }
  if (map[e.code]) {
    review(map[e.code], e)
  }
}

onMounted(() => {
  load()
  window.addEventListener('keydown', handleKey)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKey)
})
</script>

<style scoped>
.study-page {
  min-height: calc(100vh - 100px);
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 16px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  color: #fff;
}
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}
.progress-info {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 300px;
}
.progress-text {
  font-size: 14px;
  color: #a0a0b0;
  white-space: nowrap;
}
.stats-info {
  display: flex;
  gap: 12px;
}
.stat-chip {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
}
.stat-chip.xp {
  background: rgba(64, 158, 255, 0.2);
  color: #409eff;
}
.stat-chip.streak {
  background: rgba(255, 140, 0, 0.2);
  color: #ff8c00;
}
.stat-chip.voice {
  background: rgba(103, 194, 58, 0.2);
  color: #67c23a;
  cursor: pointer;
}
.voice-setting {
  cursor: pointer;
}
.center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a0a0b0;
}
.done-box {
  text-align: center;
}
.done-icon {
  font-size: 64px;
  margin-bottom: 16px;
}
.done-box h2 {
  margin: 0 0 8px;
  color: #fff;
}
.card-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  perspective: 1200px;
  cursor: pointer;
}
.card {
  width: 100%;
  max-width: 560px;
  min-height: 380px;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.5s;
}
.card.flipped {
  transform: rotateY(180deg);
}
.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 40px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}
.card-back {
  transform: rotateY(180deg);
  background: rgba(255, 255, 255, 0.12);
}
.word-main {
  font-size: 56px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  margin-top: 60px;
}
.phonetic {
  text-align: center;
  font-size: 20px;
  color: #a0a0b0;
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
}
.sound-icon {
  font-size: 22px;
  color: #409eff;
}
.hint {
  text-align: center;
  color: #606070;
  font-size: 14px;
  margin-top: auto;
}
.word-class {
  color: #e6a23c;
  font-size: 16px;
  font-weight: bold;
}
.meaning {
  font-size: 20px;
  line-height: 1.7;
  margin: 12px 0 20px;
  color: #fff;
  white-space: pre-wrap;
}
.section {
  margin-bottom: 16px;
}
.section-title {
  font-size: 13px;
  color: #409eff;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.example, .phrase {
  font-size: 14px;
  color: #c0c0d0;
  line-height: 1.6;
  margin-bottom: 6px;
  padding-left: 12px;
  border-left: 2px solid rgba(64, 158, 255, 0.4);
}
.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 30px;
}
.rate-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 14px 24px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
  min-width: 100px;
  position: relative;
}
.rate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}
.rate-btn.again { background: #f56c6c; color: #fff; }
.rate-btn.hard { background: #e6a23c; color: #fff; }
.rate-btn.good { background: #409eff; color: #fff; }
.rate-btn.easy { background: #67c23a; color: #fff; }
.rate-label {
  font-size: 16px;
  font-weight: bold;
}
.rate-xp {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 4px;
}
.rate-key {
  position: absolute;
  top: 6px;
  right: 8px;
  font-size: 11px;
  opacity: 0.6;
}
.xp-float {
  position: fixed;
  color: #ffd700;
  font-size: 20px;
  font-weight: bold;
  pointer-events: none;
  z-index: 100;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}
.xp-float-enter-active {
  transition: all 0.8s ease-out;
}
.xp-float-enter-from {
  opacity: 0;
  transform: translateY(0);
}
.xp-float-enter-to {
  opacity: 1;
  transform: translateY(-40px);
}
</style>
