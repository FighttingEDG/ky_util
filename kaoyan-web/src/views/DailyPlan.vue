<template>
  <div class="plan-page">
    <div class="header">
      <h2>每日计划</h2>
      <el-date-picker v-model="selectedDate" type="date" value-format="YYYY-MM-DD" @change="fetch" />
    </div>

    <div class="stats">
      <el-statistic title="总任务" :value="stats.total" />
      <el-statistic title="已完成" :value="stats.done" />
      <el-statistic title="预计时长(分)" :value="stats.totalEstimated" />
      <el-statistic title="实际专注(分)" :value="stats.totalActual" />
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="openDialog()">+ 新增任务</el-button>
      <el-button @click="$router.push('/focus')">⏱ 开始专注</el-button>
      <el-button @click="$router.push('/focus/stats')">📊 专注统计</el-button>
    </div>

    <el-card v-for="task in tasks" :key="task.id" class="task-card" :class="task.status">
      <div class="task-row">
        <el-checkbox
          :model-value="task.status === 'done'"
          @change="(val) => toggleStatus(task, val)"
          size="large"
        />
        <div class="task-main">
          <div class="task-title" :class="{ done: task.status === 'done' }">
            {{ task.title }}
            <el-tag v-if="task.subjectName" size="small" type="info">{{ task.subjectName }}</el-tag>
            <el-tag :type="priorityType(task.priority)" size="small">{{ priorityLabel(task.priority) }}</el-tag>
          </div>
          <div v-if="task.content" class="task-content">{{ task.content }}</div>
          <div class="task-meta">
            预计 {{ task.estimatedMinutes }} 分钟 | 实际 {{ task.actualMinutes }} 分钟
          </div>
        </div>
        <div class="task-actions">
          <el-button size="small" type="success" @click="startFocus(task)">专注</el-button>
          <el-button size="small" @click="openDialog(task)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(task.id)">删除</el-button>
        </div>
      </div>
    </el-card>

    <el-empty v-if="tasks.length === 0" description="今天还没有任务，新增一个吧" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="任务标题" />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="form.subjectId" placeholder="选择科目（可选）" clearable>
            <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="任务内容" />
        </el-form-item>
        <el-form-item label="预计时长">
          <el-input-number v-model="form.estimatedMinutes" :min="5" :step="5" /> 分钟
        </el-form-item>
        <el-form-item label="优先级">
          <el-rate v-model="form.priority" :max="3" :colors="['#67C23A', '#E6A23C', '#F56C6C']" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTasks, createTask, updateTask, deleteTask, updateTaskStatus, getDayStats } from '../api/task.js'
import { getSubjects } from '../api/card.js'

const router = useRouter()
const tasks = ref([])
const subjects = ref([])
const selectedDate = ref(new Date().toISOString().split('T')[0])
const stats = ref({ total: 0, done: 0, totalEstimated: 0, totalActual: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const form = ref({
  title: '',
  subjectId: null,
  content: '',
  estimatedMinutes: 25,
  priority: 2,
  taskDate: selectedDate.value
})

const priorityType = (p) => {
  if (p === 3) return 'danger'
  if (p === 2) return 'warning'
  return 'success'
}

const priorityLabel = (p) => {
  if (p === 3) return '高'
  if (p === 2) return '中'
  return '低'
}

const fetch = async () => {
  try {
    const res = await getTasks({ date: selectedDate.value })
    tasks.value = res.data || []
    const statsRes = await getDayStats(selectedDate.value)
    stats.value = statsRes.data
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const openDialog = (task = null) => {
  if (task) {
    isEdit.value = true
    currentId.value = task.id
    form.value = {
      title: task.title,
      subjectId: task.subjectId,
      content: task.content,
      estimatedMinutes: task.estimatedMinutes,
      priority: task.priority,
      taskDate: selectedDate.value
    }
  } else {
    isEdit.value = false
    currentId.value = null
    form.value = {
      title: '',
      subjectId: null,
      content: '',
      estimatedMinutes: 25,
      priority: 2,
      taskDate: selectedDate.value
    }
  }
  dialogVisible.value = true
}

const submit = async () => {
  try {
    if (isEdit.value) {
      await updateTask(currentId.value, form.value)
    } else {
      await createTask(form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetch()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const toggleStatus = async (task, val) => {
  try {
    await updateTaskStatus(task.id, val ? 'done' : 'todo')
    fetch()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const startFocus = (task) => {
  router.push({
    path: '/focus',
    query: {
      taskId: task.id,
      subjectId: task.subjectId,
      title: task.title,
      duration: task.estimatedMinutes
    }
  })
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这个任务吗？', '提示', { type: 'warning' })
    await deleteTask(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message)
    }
  }
}

onMounted(async () => {
  const res = await getSubjects()
  subjects.value = res.data || []
  fetch()
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
}
.task-card {
  margin-bottom: 12px;
}
.task-card.done {
  opacity: 0.7;
}
.task-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.task-main {
  flex: 1;
}
.task-title {
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}
.task-title.done {
  text-decoration: line-through;
  color: #909399;
}
.task-content {
  color: #606266;
  margin-top: 6px;
  font-size: 14px;
}
.task-meta {
  color: #909399;
  font-size: 13px;
  margin-top: 6px;
}
.task-actions {
  display: flex;
  gap: 8px;
}
.plan-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
</style>
