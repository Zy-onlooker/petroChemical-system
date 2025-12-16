<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h2>石化工业实时监测仪表盘</h2>
      <div class="dashboard-stats">
        <div class="stat-card">
          <span class="stat-label">总传感器</span>
          <span class="stat-value">{{ Object.keys(sensorData).length }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">正常运行</span>
          <span class="stat-value">{{ normalCount }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">警告</span>
          <span class="stat-value">{{ warningCount }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">危险</span>
          <span class="stat-value">{{ dangerCount }}</span>
        </div>
      </div>
    </div>
    <div class="sensor-grid">
      <SensorCard
        v-for="(sensor, sensorId) in sensorData"
        :key="sensorId"
        :sensor="sensor"
        :sensorId="sensorId"
      />
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
.dashboard {
  width: 100%;
}

.sensor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.dashboard-header {
  margin-bottom: 1.5rem;
}

.dashboard-header h2 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1rem;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.3rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

@media (max-width: 768px) {
  .sensor-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>