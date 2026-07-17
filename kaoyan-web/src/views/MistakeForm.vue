<template>
  <div class="form-page">
    <h2>{{ isEdit ? '编辑错题' : '新增错题' }}</h2>
    <el-form :model="form" label-width="90px" class="form">
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

      <el-form-item label="题目">
        <el-input
          v-model="form.question"
          type="textarea"
          :rows="4"
          placeholder="输入题目内容"
        />
      </el-form-item>

      <el-form-item label="正确答案">
        <el-input
          v-model="form.correctAnswer"
          type="textarea"
          :rows="4"
          placeholder="输入正确答案"
        />
      </el-form-item>

      <el-form-item label="错选答案">
        <el-input
          v-model="form.wrongAnswer"
          type="textarea"
          :rows="2"
          placeholder="输入当时错选的答案（可选）"
        />
      </el-form-item>

      <el-form-item label="解析">
        <el-input
          v-model="form.analysis"
          type="textarea"
          :rows="4"
          placeholder="输入解析或错因分析"
        />
      </el-form-item>

      <el-form-item label="错误原因">
        <el-select v-model="form.wrongReason" placeholder="选择错误原因">
          <el-option
            v-for="r in reasons"
            :key="r"
            :label="r"
            :value="r"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="知识点">
        <el-input v-model="form.tags" placeholder="多个知识点用逗号分隔" />
      </el-form-item>

      <el-form-item label="难度">
        <el-rate v-model="form.difficulty" :max="5" />
      </el-form-item>

      <el-form-item label="来源">
        <el-input v-model="form.source" placeholder="如：2024 真题、张宇 1000 题" />
      </el-form-item>

      <el-form-item label="图片">
        <FilePicker v-model="form.imageUrl" button-text="上传题目/答案图片" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
        <el-button @click="$router.push('/mistakes')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createMistake, getMistake, updateMistake } from '../api/mistake.js'
import { getSubjects } from '../api/card.js'
import FilePicker from '../components/FilePicker.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const subjects = ref([])
const reasons = ['概念不清', '计算失误', '审题不清', '公式记错', '其他']

const form = ref({
  subjectId: null,
  question: '',
  correctAnswer: '',
  wrongAnswer: '',
  analysis: '',
  wrongReason: '',
  tags: '',
  difficulty: 3,
  source: '',
  imageUrl: ''
})

const loadSubjects = async () => {
  const res = await getSubjects()
  subjects.value = res.data || []
}

const loadMistake = async () => {
  if (!isEdit.value) return
  const res = await getMistake(route.params.id)
  const m = res.data
  form.value = {
    subjectId: m.subjectId,
    question: m.question,
    correctAnswer: m.correctAnswer,
    wrongAnswer: m.wrongAnswer,
    analysis: m.analysis,
    wrongReason: m.wrongReason,
    tags: m.tags,
    difficulty: m.difficulty,
    source: m.source,
    imageUrl: m.imageUrl || ''
  }
}

const submit = async () => {
  try {
    if (isEdit.value) {
      await updateMistake(route.params.id, form.value)
    } else {
      await createMistake(form.value)
    }
    ElMessage.success('保存成功')
    router.push('/mistakes')
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(async () => {
  await loadSubjects()
  await loadMistake()
})
</script>

<style scoped>
.form-page {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  padding: 30px;
  border-radius: 12px;
}
.form {
  margin-top: 20px;
}
</style>
