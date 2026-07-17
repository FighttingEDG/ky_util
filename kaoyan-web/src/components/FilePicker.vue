<template>
  <div>
    <el-button type="primary" @click="visible = true">{{ buttonText }}</el-button>
    <div v-if="modelValue" class="preview">
      <img :src="modelValue" class="preview-img" />
      <el-button size="small" type="danger" @click="clear">清除</el-button>
    </div>

    <el-dialog v-model="visible" title="选择图片" width="900px" top="5vh">
      <FileManager @select="onSelect" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FileManager from '../views/FileManager.vue'

const props = defineProps({
  modelValue: String,
  buttonText: {
    type: String,
    default: '选择图片'
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)

const onSelect = (url) => {
  emit('update:modelValue', url)
  visible.value = false
}

const clear = () => {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.preview {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.preview-img {
  max-width: 200px;
  max-height: 120px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}
</style>
