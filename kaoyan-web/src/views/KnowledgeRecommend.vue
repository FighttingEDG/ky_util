<template>
  <div class="recommend-page">
    <div class="header">
      <h2>薄弱点推荐</h2>
      <el-button type="primary" @click="$router.push('/knowledge/graph')">查看图谱</el-button>
    </div>

    <el-card v-if="recommendPath.length > 0" class="path-card">
      <template #header>推荐学习路径</template>
      <el-steps :active="recommendPath.length" finish-status="success">
        <el-step v-for="node in recommendPath" :key="node.id" :title="node.name" />
      </el-steps>
    </el-card>

    <el-card>
      <template #header>薄弱知识点 Top 20</template>
      <el-empty v-if="nodes.length === 0" description="暂无数据" />
      <el-table v-else :data="nodes" stripe>
        <el-table-column prop="name" label="知识点" />
        <el-table-column prop="subjectName" label="科目" width="120" />
        <el-table-column label="掌握率" width="100">
          <template #default="{ row }">
            <el-tag :type="masteryType(row.masteryRate)">{{ row.masteryRate }}%</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stage" label="阶段" width="80" />
        <el-table-column prop="mistakeCount" label="错题数" width="90" />
        <el-table-column prop="score" label="薄弱分" width="90" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="goReview">去复习</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRecommendNodes, getKnowledgeGraph } from '../api/knowledge.js'

const router = useRouter()
const nodes = ref([])
const recommendPath = ref([])

const masteryType = (rate) => {
  if (rate < 30) return 'danger'
  if (rate < 70) return 'warning'
  return 'success'
}

const load = async () => {
  try {
    const res = await getRecommendNodes()
    nodes.value = res.data || []
    buildPath()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const buildPath = async () => {
  if (nodes.value.length === 0) return
  try {
    const graphRes = await getKnowledgeGraph()
    const edges = graphRes.data.edges || []
    const first = nodes.value[0]
    const path = [first]
    let current = first
    const visited = new Set([current.id])
    while (true) {
      const pre = edges.find(e => e.target === current.id && !visited.has(e.source))
      if (!pre) break
      const preNode = nodes.value.find(n => n.id === pre.source)
      if (!preNode) break
      path.unshift(preNode)
      visited.add(preNode.id)
      current = preNode
    }
    recommendPath.value = path
  } catch (e) {
    console.error(e)
  }
}

const goReview = () => {
  router.push('/knowledge/review')
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
.path-card {
  margin-bottom: 20px;
}
.recommend-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
</style>
