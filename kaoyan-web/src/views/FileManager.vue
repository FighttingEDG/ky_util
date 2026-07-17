<template>
  <div class="file-page">
    <div class="header">
      <h2>文件管理</h2>
      <div class="toolbar">
        <el-button type="primary" @click="showMkdirDialog = true">+ 新建文件夹</el-button>
        <el-upload
          :show-file-list="false"
          :before-upload="beforeUpload"
          :http-request="customUpload"
        >
          <el-button type="success">上传图片到当前目录</el-button>
        </el-upload>
      </div>
    </div>

    <div class="breadcrumb">
      <el-button link @click="goPath('')">root</el-button>
      <span v-for="(part, index) in pathParts" :key="index">
        /
        <el-button link @click="goPath(pathUpTo(index))">{{ part }}</el-button>
      </span>
    </div>

    <el-row :gutter="20" class="content">
      <el-col :span="6" class="tree-col">
        <el-tree
          :data="treeData"
          :props="{ label: 'name', children: 'children' }"
          default-expand-all
          @node-click="handleTreeClick"
        />
      </el-col>

      <el-col :span="18">
        <div class="file-grid">
          <div
            v-for="file in files"
            :key="file.objectName"
            class="file-card"
            @click="selectFile(file)"
          >
            <img v-if="isImage(file.objectName)" :src="file.url" class="thumb" />
            <div v-else class="file-icon">📄</div>
            <div class="file-name">{{ fileName(file.objectName) }}</div>
            <el-button
              size="small"
              type="danger"
              class="delete-btn"
              @click.stop="remove(file.objectName)"
            >删除</el-button>
          </div>
        </div>
        <el-empty v-if="files.length === 0" description="当前目录为空" />
      </el-col>
    </el-row>

    <el-dialog v-model="showMkdirDialog" title="新建文件夹" width="400px">
      <el-input v-model="newDirName" placeholder="文件夹名称" />
      <template #footer>
        <el-button @click="showMkdirDialog = false">取消</el-button>
        <el-button type="primary" @click="createDir">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFileTree, listFiles, uploadFile, mkdir, deleteFile } from '../api/file.js'

const currentPrefix = ref('')
const tree = ref([])
const files = ref([])
const showMkdirDialog = ref(false)
const newDirName = ref('')

const emit = defineEmits(['select'])

const pathParts = computed(() => {
  return currentPrefix.value.split('/').filter(Boolean)
})

const pathUpTo = (index) => {
  return pathParts.value.slice(0, index + 1).join('/') + '/'
}

const treeData = computed(() => buildTree(tree.value))

const buildTree = (paths) => {
  const root = { name: 'kaoyan', fullPath: '', children: [] }
  paths.forEach(p => {
    const parts = p.split('/').filter(Boolean)
    let node = root
    parts.forEach((part, idx) => {
      const fullPath = parts.slice(0, idx + 1).join('/') + '/'
      let child = node.children.find(c => c.fullPath === fullPath)
      if (!child) {
        child = { name: part, fullPath, children: [] }
        node.children.push(child)
      }
      node = child
    })
  })
  return [root]
}

const isImage = (name) => /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(name)

const fileName = (name) => {
  const idx = name.lastIndexOf('/')
  return idx >= 0 ? name.substring(idx + 1) : name
}

const loadTree = async () => {
  const res = await getFileTree()
  tree.value = res.data || []
}

const loadFiles = async () => {
  const res = await listFiles(currentPrefix.value)
  files.value = res.data || []
}

const goPath = (path) => {
  currentPrefix.value = path
  loadFiles()
}

const handleTreeClick = (node) => {
  currentPrefix.value = node.fullPath || ''
  loadFiles()
}

const beforeUpload = (file) => {
  if (!isImage(file.name)) {
    ElMessage.warning('只能上传图片')
    return false
  }
  return true
}

const customUpload = async ({ file }) => {
  try {
    const res = await uploadFile(file, currentPrefix.value)
    ElMessage.success('上传成功')
    await loadTree()
    await loadFiles()
    return res
  } catch (e) {
    ElMessage.error(e.message)
    throw e
  }
}

const createDir = async () => {
  if (!newDirName.value) return
  try {
    const prefix = currentPrefix.value + newDirName.value + '/'
    await mkdir(prefix)
    ElMessage.success('创建成功')
    showMkdirDialog.value = false
    newDirName.value = ''
    await loadTree()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const remove = async (objectName) => {
  try {
    await ElMessageBox.confirm('确定删除这个文件吗？', '提示', { type: 'warning' })
    await deleteFile(objectName)
    ElMessage.success('删除成功')
    await loadFiles()
    await loadTree()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message)
    }
  }
}

const selectFile = (file) => {
  emit('select', file.url)
}

watch(currentPrefix, loadFiles)

onMounted(async () => {
  await loadTree()
  await loadFiles()
})
</script>

<style scoped>
.file-page {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  min-height: 600px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.toolbar {
  display: flex;
  gap: 12px;
}
.breadcrumb {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.content {
  margin-top: 10px;
}
.tree-col {
  border-right: 1px solid #ebeef5;
  min-height: 500px;
}
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}
.file-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  position: relative;
  transition: box-shadow 0.2s;
}
.file-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.thumb {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}
.file-icon {
  font-size: 48px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.file-name {
  margin-top: 8px;
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.delete-btn {
  margin-top: 8px;
}
</style>
