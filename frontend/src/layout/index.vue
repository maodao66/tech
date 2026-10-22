<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">科技金融平台</div>
      <el-menu router :default-active="$route.path">
        <el-menu-item index="/">首页</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">科技金融管理平台</el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}
.aside {
  border-right: 1px solid #ebeef5;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}
.header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
}
</style>
