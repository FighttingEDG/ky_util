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
      <el-sub-menu index="/plan">
        <template #title>每日计划</template>
        <el-menu-item index="/plan">任务清单</el-menu-item>
        <el-menu-item index="/focus">专注计时</el-menu-item>
        <el-menu-item index="/focus/stats">专注统计</el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="/knowledge">
        <template #title>408 图谱</template>
        <el-menu-item index="/knowledge/graph">知识图谱</el-menu-item>
        <el-menu-item index="/knowledge/nodes">知识点管理</el-menu-item>
        <el-menu-item index="/knowledge/review">今日复习</el-menu-item>
        <el-menu-item index="/knowledge/recommend">薄弱推荐</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/files">文件管理</el-menu-item>
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
