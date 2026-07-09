<template>
  <div class="form-page">
    <h2>{{ isEdit ? '编辑卡片' : '新增卡片' }}</h2>
    <el-form :model="form" label-width="80px" class="form">
      <el-form-item label="科目">
        <el-select v-model="form.subjectId" placeholder="选择科目">
          <el-option
            v-for="s in subjects"
            :key="s.id"
            :label="s.name"
            :value="s.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="正面">
        <el-input
          v-model="form.front"
          type="textarea"
          :rows="4"
          placeholder="输入问题或提示"
        />
      </el-form-item>

      <el-form-item label="背面">
        <el-input
          v-model="form.back"
          type="textarea"
          :rows="6"
          placeholder="输入答案"
        />
      </el-form-item>

      <el-form-item label="标签">
        <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
      </el-form-item>

      <el-form-item label="难度">
        <el-rate v-model="form.difficulty" :max="5" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
        <el-button @click="$router.push('/cards')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSubjects, createCard, getCard, updateCard } from '../api/card.js'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const subjects = ref([])
const form = ref({
  subjectId: null,
  front: '',
  back: '',
  tags: '',
  difficulty: 3
})

const loadSubjects = async () => {
  const res = await getSubjects()
  subjects.value = res.data || []
}

const loadCard = async () => {
  if (!isEdit.value) return
  const res = await getCard(route.params.id)
  const card = res.data
  form.value = {
    subjectId: card.subjectId,
    front: card.front,
    back: card.back,
    tags: card.tags,
    difficulty: card.difficulty
  }
}

const submit = async () => {
  try {
    if (isEdit.value) {
      await updateCard(route.params.id, form.value)
    } else {
      await createCard(form.value)
    }
    ElMessage.success('保存成功')
    router.push('/cards')
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(async () => {
  await loadSubjects()
  await loadCard()
})
</script>

<style scoped>
.form-page {
  max-width: 700px;
  margin: 0 auto;
  background: #fff;
  padding: 30px;
  border-radius: 12px;
}
.form {
  margin-top: 20px;
}
</style>
