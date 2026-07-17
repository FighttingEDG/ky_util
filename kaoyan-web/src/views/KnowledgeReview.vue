<template>
  <div class="review-page">
    <div class="header">
      <h2>知识点复习</h2>
      <el-tag type="warning" size="large">剩余 {{ queue.length }} 个</el-tag>
    </div>

    <div v-if="loading" class="center">加载中...</div>
    <div v-else-if="queue.length === 0" class="center">
      <el-result icon="success" title="今日知识点复习已完成" sub-title="明天再来吧"></el-result>
    </div>
    <div v-else class="node-card">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>{{ current.name }}</span>
            <el-tag :type="masteryType(current.masteryRate)">掌握率 {{ current.masteryRate }}%</el-tag>
          </div>
        </template>
        <p class="label">知识点描述</p>
        <p class="content">{{ current.description || '暂无描述' }}</p>
      </el-card>

      <div class="actions">
        <el-button type="danger" @click="review('again')">Again（未懂）</el-button>
        <el-button type="warning" @click="review('hard')">Hard（困难）</el-button>
        <el-button type="primary" @click="review('good')">Good（掌握）</el-button>
        <el-button type="success" @click="review('easy')">Easy（熟练）</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayKnowledgeNodes, reviewKnowledgeNode } from '../api/knowledge.js'

const queue = ref([])
const currentIndex = ref(0)
const loading = ref(false)

const current = computed(() => queue.value[currentIndex.value])

const masteryType = (rate) => {
  if (rate < 30) return 'danger'
  if (rate < 70) return 'warning'
  return 'success'
}

const load = async () => {
  loading.value = true
  try {
    const res = await getTodayKnowledgeNodes()
    queue.value = res.data || []
    currentIndex.value = 0
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const review = async (result) => {
  try {
    await reviewKnowledgeNode(current.value.id, result)
    queue.value.splice(currentIndex.value, 1)
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
.node-card {
  max-width: 700px;
  margin: 0 auto;
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
}
.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}
</style>
