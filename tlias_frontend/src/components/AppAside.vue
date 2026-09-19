<script setup>
import { useRoute } from 'vue-router'

// 当前路由信息：用它让菜单高亮跟着 URL 走（刷新页面也不会丢）
const route = useRoute()
</script>

<template>
  <!-- router 属性：点击菜单项时自动跳转到 index 对应的路径 -->
  <el-menu :default-active="route.path" router class="aside-menu">
    <el-menu-item index="/index">首页</el-menu-item>

    <el-sub-menu index="clazz-group">
      <template #title>班级学员管理</template>
      <el-menu-item index="/clazz">班级管理</el-menu-item>
      <el-menu-item index="/stu">学员管理</el-menu-item>
    </el-sub-menu>

    <el-sub-menu index="system-group">
      <template #title>系统信息管理</template>
      <el-menu-item index="/dept">部门管理</el-menu-item>
      <el-menu-item index="/emp">员工管理</el-menu-item>
      <el-menu-item index="/log">日志管理</el-menu-item>
    </el-sub-menu>

    <el-sub-menu index="report-group">
      <template #title>数据统计管理</template>
      <el-menu-item index="/empReport">员工信息统计</el-menu-item>
      <el-menu-item index="/stuReport">学员信息统计</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<style scoped>
  .aside-menu {
    /* 去掉 el-menu 自带的右边框，避免与外层 el-aside 的边框叠成双线 */
    border-right: none;
  }
</style>
