<template>
  <div class="app-container">
    <!-- Header布局优化：标题+导航横向对齐 -->
    <header class="app-header">
      <div class="header-inner">
        <h1 class="header-title">石油化工监测系统</h1>
        <nav class="header-nav">
          <router-link to="/" class="nav-link">仪表盘</router-link>
          <router-link to="/blast-monitoring" class="nav-link">爆破安全监控</router-link>
          <router-link to="/environmental" class="nav-link">环境影响分析</router-link>
        </nav>
      </div>
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
/* 全局样式重置（保留原有基础，优化细节） */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft Yahei', 'PingFang SC', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.5; /* 新增：更舒适的行高 */
}

/* 容器样式（保留原有flex布局，优化最小高度） */
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa; /* 统一背景色，避免body和容器不一致 */
}

/* Header优化：增强质感，横向布局 */
.app-header {
  background: linear-gradient(135deg, whitesmoke 0%, gray 100%); /* 渐变背景，提升层次感 */
  color: white;
  padding: 0; /* 内层容器控制间距，更灵活 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* 更细腻的阴影 */
  position: sticky; /* 新增：Header吸顶，方便导航 */
  top: 0;
  z-index: 100;
}

/* Header内层容器：控制宽度和对齐 */
.header-inner {
  max-width: 1440px; /* 和子页面宽度统一 */
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between; /* 标题左，导航右 */
  align-items: center; /* 垂直居中 */
}

/* 标题样式优化 */
.header-title {
  font-size: 1.6rem;
  font-weight: 600;
  letter-spacing: 0.5px; /* 细微字间距，更易读 */
  margin: 0; /* 移除原有margin，由内层容器控制 */
}

/* 导航样式优化 */
.header-nav {
  display: flex;
  gap: 1rem; /* 缩小间距，更紧凑 */
}

/* 导航链接样式（核心交互优化） */
.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.6rem 1.2rem;
  border-radius: 8px; /* 更大圆角，更现代 */
  transition: all 0.3s ease; /* 统一过渡动画 */
  font-size: 1rem;
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

/* 导航hover效果：背景+轻微缩放 */
.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.15); /* 更细腻的透明度 */
  transform: translateY(-1px); /* 轻微上浮 */
}

/* 激活状态优化：更醒目，保留原有逻辑 */
.nav-link.router-link-exact-active {
  background-color: rgba(255, 255, 255, 0.25);
  font-weight: 600;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.3); /* 内层边框，更精致 */
}

/* 主内容区优化：统一宽度，更舒适的间距 */
.app-main {
  flex: 1;
  padding: 2rem;
  max-width: 1440px; /* 限制最大宽度，避免大屏拉伸 */
  margin: 0 auto; /* 居中 */
  width: 100%; /* 适配小屏 */
}

/* 响应式适配：移动端优化 */
@media (max-width: 768px) {
  .header-inner {
    flex-direction: column; /* 移动端标题+导航垂直排列 */
    padding: 1rem 1.5rem;
    gap: 1rem; /* 垂直间距 */
    text-align: center;
  }

  .header-title {
    font-size: 1.4rem;
  }

  .header-nav {
    width: 100%;
    justify-content: center; /* 导航居中 */
    gap: 0.8rem;
  }

  .nav-link {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }

  .app-main {
    padding: 1.5rem 1rem; /* 移动端缩小内边距 */
  }
}

/* 超小屏适配（手机竖屏） */
@media (max-width: 480px) {
  .header-nav {
    flex-wrap: wrap; /* 导航自动换行 */
  }

  .nav-link {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
}
</style>