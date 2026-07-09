<template>
  <div class="app">
    <el-menu
      :default-active="activeIndex"
      class="nav"
      mode="horizontal"
      router
    >
      <el-menu-item index="/cards">卡片管理</el-menu-item>
      <el-menu-item index="/review">今日复习</el-menu-item>
      <el-sub-menu index="/mistakes">
        <template #title>错题本</template>
        <el-menu-item index="/mistakes">错题管理</el-menu-item>
        <el-menu-item index="/mistakes/review">错题重做</el-menu-item>
      </el-sub-menu>
    </el-menu>
    <div class="main">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeIndex = computed(() => route.path)
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #f5f7fa;
}
.main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
