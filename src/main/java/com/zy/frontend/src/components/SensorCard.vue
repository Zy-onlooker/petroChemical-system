<template>
  <div class="sensor-card" :class="getStatusClass(sensor.status)">
    <div class="sensor-header">
      <h3>{{ sensorId }}</h3>
      <span class="status-indicator">{{ getStatusText(sensor.status) }}</span>
    </div>
    <div class="sensor-metrics">
      <div class="metric">
        <span class="metric-label">温度</span>
        <span class="metric-value">{{ (sensor?.data?.temperature ?? 0).toFixed(1) }}°C</span>
      </div>
      <div class="metric">
        <span class="metric-label">压力</span>
        <span class="metric-value">{{ (sensor?.data?.pressure ?? 0).toFixed(1) }} bar</span>
      </div>
      <div class="metric">
        <span class="metric-label">流量</span>
        <span class="metric-value">{{ (sensor?.data?.flow_rate ?? 0).toFixed(1) }} L/min</span>
      </div>
      <div class="metric">
        <span class="metric-label">pH值</span>
        <span class="metric-value">{{ (sensor?.data?.ph ?? 7.0).toFixed(1) }}</span>
      </div>
      <div class="metric">
        <span class="metric-label">浊度</span>
        <span class="metric-value">{{ (sensor?.data?.turbidity ?? 0).toFixed(1) }} NTU</span>
      </div>
      <div class="metric">
        <span class="metric-label">电导率</span>
        <span class="metric-value">{{ (sensor?.data?.conductivity ?? 500).toFixed(0) }} μS/cm</span>
      </div>
    </div>
    <div class="sensor-timestamp">
      {{ formatTimestamp(sensor?.data?.timestamp) }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'SensorCard',
  props: {
    sensor: {
      type: Object,
      required: true,
      // 提供默认值，避免props为空时报错
      default: () => ({
        status: 'normal',
        data: {
          temperature: 0,
          pressure: 0,
          flow_rate: 0,
          ph: 7.0,
          turbidity: 0,
          conductivity: 500,
          timestamp: Date.now()
        }
      })
    },
    sensorId: {
      type: String,
      required: true
    }
  },
  methods: {
    // 状态类名（兼容undefined）
    getStatusClass(status) {
      if (!status) return 'status-normal'
      return {
        'status-normal': status === 'normal',
        'status-warning': status === 'warning',
        'status-danger': status === 'danger'
      }
    },
    // 状态文本（兼容undefined）
    getStatusText(status) {
      const statusMap = {
        normal: '正常',
        warning: '警告',
        danger: '危险'
      }
      return statusMap[status] || '未知'
    },
    // 时间格式化（兼容undefined）
    formatTimestamp(timestamp) {
      if (!timestamp) return '暂无数据'
      try {
        return new Date(timestamp).toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
      } catch (e) {
        return '时间格式错误'
      }
    }
  }
}
</script>

<style scoped>
.sensor-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: all 0.3s ease;
}

.sensor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e8e8e8;
}

.sensor-header h3 {
  font-size: 1.2rem;
  color: #333;
  margin: 0;
}

.status-indicator {
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
}

.sensor-metrics {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.8rem;
  margin-bottom: 1rem;
}

.metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metric-label {
  color: #666;
  font-size: 0.9rem;
}

.metric-value {
  font-weight: bold;
  color: #333;
}

.sensor-timestamp {
  font-size: 0.8rem;
  color: #999;
  text-align: right;
  margin-top: 0.5rem;
}

/* Status colors */
.status-normal {
  border-left: 4px solid #52c41a;
}

.status-warning {
  border-left: 4px solid #faad14;
}

.status-danger {
  border-left: 4px solid #f5222d;
}

.status-normal .status-indicator {
  background-color: #e8f5e9;
  color: #52c41a;
}

.status-warning .status-indicator {
  background-color: #fff7e6;
  color: #faad14;
}

.status-danger .status-indicator {
  background-color: #fff1f0;
  color: #f5222d;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .sensor-metrics {
    grid-template-columns: 1fr;
  }
  .sensor-header h3 {
    font-size: 1rem;
  }
}
</style>