<template>
  <div class="graph-page">
    <div class="header">
      <h2>408 知识图谱</h2>
      <div class="legend">
        <span class="legend-item"><span class="dot red" />薄弱</span>
        <span class="legend-item"><span class="dot yellow" />一般</span>
        <span class="legend-item"><span class="dot green" />掌握</span>
      </div>
    </div>

    <div ref="chartRef" class="chart"></div>

    <el-drawer v-model="drawerVisible" title="知识点详情" width="400px">
      <div v-if="selectedNode">
        <h3>{{ selectedNode.name }}</h3>
        <p class="node-meta">科目：{{ selectedNode.subjectName || '-' }}</p>
        <p class="node-meta">掌握率：{{ selectedNode.masteryRate }}%</p>
        <p class="node-meta">阶段：{{ selectedNode.stage }}</p>
        <p v-if="selectedNode.description" class="node-desc">{{ selectedNode.description }}</p>
        <div class="drawer-actions">
          <el-button type="primary" @click="goReview">去复习</el-button>
          <el-button @click="goList">知识点列表</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getKnowledgeGraph } from '../api/knowledge.js'

const router = useRouter()
const chartRef = ref(null)
const chart = ref(null)
const drawerVisible = ref(false)
const selectedNode = ref(null)

const getNodeColor = (masteryRate) => {
  const rate = masteryRate || 0
  if (rate < 30) return '#f56c6c'
  if (rate < 70) return '#e6a23c'
  return '#67c23a'
}

const loadGraph = async () => {
  try {
    const res = await getKnowledgeGraph()
    const data = res.data

    const nodes = data.nodes.map(n => ({
      id: n.id,
      name: n.name,
      value: n.masteryRate,
      symbolSize: n.level === 1 ? 40 : n.level === 2 ? 28 : 18,
      itemStyle: { color: getNodeColor(n.masteryRate) },
      label: { show: n.level <= 2, fontSize: 12 },
      ...n
    }))

    const links = data.edges.map(e => ({
      source: e.source,
      target: e.target,
      relationType: e.relationType
    }))

    chart.value.setOption({
      tooltip: {
        formatter: (params) => {
          if (params.dataType === 'node') {
            return `${params.data.name}<br/>掌握率：${params.data.masteryRate}%`
          }
          return ''
        }
      },
      series: [{
        type: 'graph',
        layout: 'force',
        data: nodes,
        links: links,
        roam: true,
        label: { show: true },
        force: {
          repulsion: 300,
          edgeLength: 100
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: { width: 4 }
        }
      }]
    })

    chart.value.on('click', (params) => {
      if (params.dataType === 'node') {
        selectedNode.value = params.data
        drawerVisible.value = true
      }
    })
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const goReview = () => {
  router.push('/knowledge/review')
  drawerVisible.value = false
}

const goList = () => {
  router.push('/knowledge/nodes')
  drawerVisible.value = false
}

onMounted(() => {
  chart.value = echarts.init(chartRef.value)
  loadGraph()
  window.addEventListener('resize', () => chart.value?.resize())
})

onUnmounted(() => {
  chart.value?.dispose()
})
</script>

<style scoped>
.graph-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.legend {
  display: flex;
  gap: 16px;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}
.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}
.red { background: #f56c6c; }
.yellow { background: #e6a23c; }
.green { background: #67c23a; }
.chart {
  flex: 1;
  min-height: 500px;
}
.node-meta {
  color: #606266;
  margin: 8px 0;
}
.node-desc {
  color: #303133;
  line-height: 1.6;
  margin-top: 16px;
}
.drawer-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}
</style>
