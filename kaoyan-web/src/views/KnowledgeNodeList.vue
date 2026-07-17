<template>
  <div class="list-page">
    <div class="header">
      <h2>知识点管理</h2>
      <el-button type="primary" @click="openDialog()">+ 新增知识点</el-button>
    </div>

    <el-table :data="treeData" row-key="id" default-expand-all stripe>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" width="140" />
      <el-table-column prop="subjectName" label="科目" width="120" />
      <el-table-column label="掌握率" width="100">
        <template #default="{ row }">
          <el-tag :type="masteryType(row.masteryRate)">{{ row.masteryRate }}%</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stage" label="阶段" width="80" />
      <el-table-column label="下次复习" width="120">
        <template #default="{ row }">
          {{ formatDate(row.nextReviewDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑知识点' : '新增知识点'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="知识点名称" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" placeholder="唯一编码" />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="form.subjectId" placeholder="选择科目">
            <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="父节点">
          <el-select v-model="form.parentId" placeholder="根节点" clearable>
            <el-option v-for="n in flatNodes" :key="n.id" :label="n.name" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="层级">
          <el-input-number v-model="form.level" :min="1" :max="3" />
        </el-form-item>
        <el-form-item label="权重">
          <el-input-number v-model="form.weight" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getKnowledgeNodes, createKnowledgeNode, updateKnowledgeNode, deleteKnowledgeNode } from '../api/knowledge.js'
import { getSubjects } from '../api/card.js'

const treeData = ref([])
const flatNodes = ref([])
const subjects = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const form = ref({
  name: '',
  code: '',
  subjectId: null,
  parentId: null,
  level: 3,
  weight: 5,
  description: ''
})

const masteryType = (rate) => {
  if (rate < 30) return 'danger'
  if (rate < 70) return 'warning'
  return 'success'
}

const formatDate = (date) => {
  if (!date || date === '9999-12-31') return '-'
  return date
}

const flatten = (nodes) => {
  const result = []
  const walk = (list) => {
    list.forEach(n => {
      result.push(n)
      if (n.children) walk(n.children)
    })
  }
  walk(nodes)
  return result
}

const fetch = async () => {
  try {
    const res = await getKnowledgeNodes()
    treeData.value = res.data || []
    flatNodes.value = flatten(treeData.value)
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const openDialog = (node = null) => {
  if (node) {
    isEdit.value = true
    currentId.value = node.id
    form.value = {
      name: node.name,
      code: node.code,
      subjectId: node.subjectId,
      parentId: node.parentId,
      level: node.level,
      weight: node.weight,
      description: node.description
    }
  } else {
    isEdit.value = false
    currentId.value = null
    form.value = {
      name: '',
      code: '',
      subjectId: null,
      parentId: null,
      level: 3,
      weight: 5,
      description: ''
    }
  }
  dialogVisible.value = true
}

const submit = async () => {
  try {
    if (isEdit.value) {
      await updateKnowledgeNode(currentId.value, form.value)
    } else {
      await createKnowledgeNode(form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetch()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这个知识点吗？', '提示', { type: 'warning' })
    await deleteKnowledgeNode(id)
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
.list-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
</style>
