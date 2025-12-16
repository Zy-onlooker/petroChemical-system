<template>
  <div class="app-container">
    <header class="app-header">
      <h1>石化工业智能监测仿真系统</h1>
      <nav>
        <router-link to="/">仪表盘</router-link>
        <router-link to="/blast-monitoring">爆破安全监控</router-link>
        <router-link to="/environmental">环境影响分析</router-link>
      </nav>
    </header>
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f5f7fa;
  color: #333;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background-color: #1890ff;
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.app-header h1 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
}

.app-header nav {
  display: flex;
  gap: 1.5rem;
}

.app-header nav a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.app-header nav a:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.app-header nav a.router-link-exact-active {
  background-color: rgba(255, 255, 255, 0.3);
  font-weight: bold;
}

.app-main {
  flex: 1;
  padding: 2rem;
}
</style>