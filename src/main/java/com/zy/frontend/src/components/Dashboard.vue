<template>
  <div class="dashboard">
    <!-- 头部区域：标题+右上角状态卡片 -->
    <div class="dashboard-header">
      <div class="header-inner">
        <h2 class="dashboard-title">石化工业实时监测仪表盘</h2>
        <div class="right-stats">
          <div class="status-card total-status">
            <span class="status-label">总传感器</span>
            <span class="status-value">{{ Object.keys(sensorData).length }}</span>
          </div>
          <div class="status-card normal-status">
            <span class="status-label">正常运行</span>
            <span class="status-value">{{ normalCount }}</span>
          </div>
          <div class="status-card warning-status">
            <span class="status-label">警告</span>
            <span class="status-value">{{ warningCount }}</span>
          </div>
          <div class="status-card danger-status">
            <span class="status-label">危险</span>
            <span class="status-value">{{ dangerCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="content-wrapper">
      <!-- 3个传感器卡片 -->
      <div class="sensor-grid">
        <SensorCard
            v-for="([sensorId, sensor], index) in Object.entries(sensorData).slice(0, 3)"
            :key="sensorId"
            :sensor="sensor"
            :sensorId="sensorId"
        />
      </div>


    </div>
  </div>
</template>

<script>
import SensorCard from './SensorCard.vue'

export default {
  name: 'Dashboard',
  components: {
    SensorCard
  },
  props: {
    sensorData: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    normalCount() {
      return Object.values(this.sensorData).filter(s => s.status === 'normal').length
    },
    warningCount() {
      return Object.values(this.sensorData).filter(s => s.status === 'warning').length
    },
    dangerCount() {
      return Object.values(this.sensorData).filter(s => s.status === 'danger').length
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.dashboard {
  width: 100%;
  background-color: #1e1e2d;
  color: #fff;
  padding: 0 1rem;
}

/* 头部内层 */
.header-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0.8rem 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 标题 */
.dashboard-title {
  font-size: 1.5rem;
  margin: 0;
  font-weight: 600;
}

/* 右上角状态卡片 */
.right-stats {
  display: flex;
  gap: 0.8rem;
  align-items: center;
}

.status-card {
  background: #2d2d42;
  padding: 0.5rem 0.8rem;
  border-radius: 6px;
  text-align: center;
  min-width: 80px;
}

.status-label {
  display: block;
  font-size: 0.75rem;
  color: #888;
  margin-bottom: 0.2rem;
}

.status-value {
  font-size: 1rem;
  font-weight: bold;
}

.total-status .status-value { color: #40a9ff; }
.normal-status .status-value { color: #52c41a; }
.warning-status .status-value { color: #faad14; }
.danger-status .status-value { color: #f5222d; }

/* 内容容器 */
.content-wrapper {
  max-width: 1440px;
  margin: 0 auto;
  padding: 1rem 0;
}

/* 传感器网格 */
.sensor-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.stream-title {
  font-size: 1rem;
  margin-bottom: 0.8rem;
  color: #eee;
}

.stream-content {
  max-height: 220px;
  overflow-y: auto;
  padding-right: 0.5rem;
}

/* 数据流项样式 */
.stream-item {
  display: flex;
  justify-content: flex-start;
  gap: 1.5rem;
  padding: 0.4rem 0;
  border-bottom: 1px solid #3d3d58;
}

.stream-time {
  color: #888;
  min-width: 60px;
}

.stream-sensor {
  min-width: 100px;
}

.stream-value {
  color: #40a9ff;
  font-weight: bold;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .sensor-grid {
    grid-template-columns: 1fr;
  }
  .right-stats {
    gap: 0.5rem;
  }
  .status-card {
    min-width: 60px;
    padding: 0.4rem 0.6rem;
  }
}
</style>