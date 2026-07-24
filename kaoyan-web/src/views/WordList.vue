<template>
  <div class="list-page">
    <div class="header">
      <h2>单词库</h2>
      <div>
        <el-button type="primary" @click="$router.push('/words/study')">📖 开始学习</el-button>
        <el-button type="warning" @click="reimport" :loading="reimporting">重新导入 Excel</el-button>
      </div>
    </div>

    <div class="toolbar">
      <el-select v-model="query.wordType" placeholder="全部词型" clearable @change="fetch">
        <el-option label="必背词" value="must" />
        <el-option label="主考词" value="exam" />
      </el-select>
      <el-select v-model="query.stage" placeholder="全部阶段" clearable @change="fetch">
        <el-option v-for="s in 8" :key="s-1" :label="`阶段 ${s-1}`" :value="s-1" />
      </el-select>
      <el-input
        v-model="query.keyword"
        placeholder="搜索单词或释义"
        clearable
        style="width: 220px"
        @change="fetch"
      />
    </div>

    <el-table :data="words" v-loading="loading" stripe @row-click="toggleExpand">
      <el-table-column prop="word" label="单词" width="140">
        <template #default="{ row }">
          <span class="word-text">{{ row.word }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="phonetic" label="音标" width="150" />
      <el-table-column prop="wordClass" label="词性" width="80" />
      <el-table-column prop="meaning" label="释义" min-width="200">
        <template #default="{ row }">
          <div class="ellipsis">{{ row.meaning }}</div>
        </template>
      </el-table-column>
      <el-table-column label="词型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.wordType === 'exam' ? 'warning' : 'success'" size="small">
            {{ row.wordType === 'exam' ? '主考' : '必背' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stage" label="阶段" width="70" />
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="detail">
            <p><strong>释义：</strong>{{ row.meaning }}</p>
            <div v-if="detailMap[row.id]?.examples?.length">
              <strong>考点例句：</strong>
              <div v-for="(ex, i) in detailMap[row.id].examples" :key="i" class="detail-line">{{ ex }}</div>
            </div>
            <div v-if="detailMap[row.id]?.phrases?.length">
              <strong>词组：</strong>
              <div v-for="(p, i) in detailMap[row.id].phrases" :key="i" class="detail-line">{{ p }}</div>
            </div>
            <p v-if="row.rootMemo"><strong>词根：</strong>{{ row.rootMemo }}</p>
            <p v-if="row.examMeaning"><strong>主考词义：</strong>{{ row.examMeaning }}</p>
            <p v-if="row.derivatives"><strong>派生：</strong>{{ row.derivatives }}</p>
            <p v-if="row.synonyms"><strong>近义：</strong>{{ row.synonyms }}</p>
            <p v-if="row.antonyms"><strong>反义：</strong>{{ row.antonyms }}</p>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="fetch"
      @size-change="fetch"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWords, getWord, reimportWords } from '../api/word.js'

const words = ref([])
const total = ref(0)
const loading = ref(false)
const reimporting = ref(false)
const detailMap = ref({})

const query = reactive({
  wordType: null,
  stage: null,
  keyword: '',
  page: 1,
  size: 10
})

const fetch = async () => {
  loading.value = true
  try {
    const res = await getWords({
      wordType: query.wordType,
      stage: query.stage,
      keyword: query.keyword,
      page: query.page - 1,
      size: query.size
    })
    words.value = res.data.content
    total.value = res.data.totalElements
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const toggleExpand = async (row) => {
  if (detailMap.value[row.id]) return
  try {
    const res = await getWord(row.id)
    detailMap.value[row.id] = res.data
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const reimport = async () => {
  try {
    await ElMessageBox.confirm('重新导入会清空当前单词数据（包括复习进度），确定继续吗？', '警告', { type: 'warning' })
    reimporting.value = true
    await reimportWords()
    ElMessage.success('导入完成')
    detailMap.value = {}
    fetch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message)
    }
  } finally {
    reimporting.value = false
  }
}

watch(() => [query.wordType, query.stage, query.keyword], () => {
  query.page = 1
  fetch()
}, { deep: true })

onMounted(fetch)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.word-text {
  font-weight: bold;
  color: #409eff;
  font-size: 16px;
}
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.detail {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}
.detail-line {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  padding-left: 12px;
  margin: 4px 0;
}
.list-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
}
</style>
