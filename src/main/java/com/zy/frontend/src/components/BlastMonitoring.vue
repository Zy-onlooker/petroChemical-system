<template>
  <div class="blast-monitoring">
    <h2>爆破安全监控系统</h2>
    <div class="monitoring-grid">
      <div class="blast-zone" v-for="(zone, zoneId) in blastData" :key="zoneId">
        <h3>爆破区域 {{ zoneId }}</h3>
        <div class="zone-metrics">
          <div class="metric-row">
            <div class="metric-item">
              <span class="metric-label">振动水平</span>
              <span class="metric-value">{{ (zone.vibrationLevel || 0).toFixed(1) }} mm/s</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">噪音水平</span>
              <span class="metric-value">{{ (zone.noiseLevel || 0).toFixed(1) }} dB</span>
            </div>
          </div>
          <div class="metric-row">
            <div class="metric-item">
              <span class="metric-label">空气质量</span>
              <span class="metric-value">{{ (zone.airQuality || 0).toFixed(0) }} AQI</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">人员数量</span>
              <span class="metric-value">{{ zone.personnelCount || 0 }} 人</span>
            </div>
          </div>
          <div class="metric-row">
            <div class="metric-item">
              <span class="metric-label">可燃气体</span>
              <span class="metric-value">{{ (zone.explosiveGas || 0).toFixed(1) }} ppm</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">距离爆破点</span>
              <span class="metric-value">{{ (zone.distanceToBlast || 0).toFixed(0) }} 米</span>
            </div>
          </div>
        </div>
        <div class="safety-status" :class="getSafetyClass(zone.safetyStatus)">
          安全状态: {{ zone.safetyStatus || '未知' }}
        </div>
        <div class="timestamp">
          更新时间: {{ formatTimestamp(zone.timestamp) }}
        </div>
      </div>
    </div>
    <!-- 图表区域（有数据才显示） -->
    <div v-if="Object.keys(blastData).length > 0" class="visualization-section">
      <div class="chart-container">
        <canvas ref="vibrationChart"></canvas>
      </div>
      <div class="chart-container">
        <canvas ref="noiseChart"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

export default {
  name: 'BlastMonitoring',
  data() {
    return {
      blastData: {},
      vibrationChart: null,
      noiseChart: null,
      updateInterval: null
    }
  },
  mounted() {
    // 初始化请求
    this.fetchBlastData()
    // 初始化图表（延迟确保DOM加载完成）
    this.$nextTick(() => this.setupCharts())
    // 定时刷新（5秒一次）
    this.updateInterval = setInterval(this.fetchBlastData, 5000)
  },
  beforeUnmount() {
    // 清理定时器和图表实例，防止内存泄漏
    if (this.updateInterval) {
      clearInterval(this.updateInterval)
    }
    if (this.vibrationChart) {
      this.vibrationChart.destroy()
    }
    if (this.noiseChart) {
      this.noiseChart.destroy()
    }
  },
  methods: {
    /**
     * 获取爆破数据（添加超时/错误处理）
     */
    async fetchBlastData() {
      try {
        const controller = new AbortController()
        const timeoutId = setTimeout(() => controller.abort(), 5000)
        const response = await fetch('/api/blast-data', {
          method: 'GET',
          signal: controller.signal,
          headers: {
            'Content-Type': 'application/json'
          }
        })
        clearTimeout(timeoutId)

        if (!response.ok) {
          throw new Error(`请求失败：${response.status} ${response.statusText}`)
        }

        const data = await response.json()
        this.blastData = data
        this.updateCharts()
      } catch (error) {
        console.error('获取爆破数据失败:', error)
        // 修正端口提示为8080
        alert(`数据加载失败：${error.message}\n请检查后端服务是否启动在 8080 端口`)
      }
    },

    /**
     * 初始化图表
     */
    setupCharts() {
      const vibrationCtx = this.$refs.vibrationChart?.getContext('2d')
      const noiseCtx = this.$refs.noiseChart?.getContext('2d')

      // 防止DOM未加载的情况
      if (!vibrationCtx || !noiseCtx) return

      // 振动水平图表
      this.vibrationChart = new Chart(vibrationCtx, {
        type: 'line',
        data: {
          labels: [],
          datasets: [{
            label: '平均振动水平 (mm/s)',
            data: [],
            borderColor: '#1890ff',
            backgroundColor: 'rgba(24, 144, 255, 0.1)',
            tension: 0.3,
            fill: true
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false, // 自适应容器高度
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              title: {
                display: true,
                text: '振动水平 (mm/s)'
              }
            },
            x: {
              title: {
                display: true,
                text: '时间'
              }
            }
          },
          plugins: {
            legend: {
              position: 'top'
            },
            tooltip: {
              mode: 'index',
              intersect: false
            }
          }
        }
      })

      // 噪音水平图表
      this.noiseChart = new Chart(noiseCtx, {
        type: 'line',
        data: {
          labels: [],
          datasets: [{
            label: '平均噪音水平 (dB)',
            data: [],
            borderColor: '#fa541c',
            backgroundColor: 'rgba(250, 84, 28, 0.1)',
            tension: 0.3,
            fill: true
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              title: {
                display: true,
                text: '噪音水平 (dB)'
              }
            },
            x: {
              title: {
                display: true,
                text: '时间'
              }
            }
          },
          plugins: {
            legend: {
              position: 'top'
            },
            tooltip: {
              mode: 'index',
              intersect: false
            }
          }
        }
      })
    },

    /**
     * 更新图表数据
     */
    updateCharts() {
      const now = new Date()
      const timeString = now.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })

      if (this.vibrationChart) {
        const avgVibration = Object.values(this.blastData).reduce(
            (sum, zone) => sum + (zone.vibrationLevel || 0), 0) / (Object.keys(this.blastData).length || 1)

        this.vibrationChart.data.labels.push(timeString)
        this.vibrationChart.data.datasets[0].data.push(avgVibration)

        if (this.vibrationChart.data.labels.length > 10) {
          this.vibrationChart.data.labels.shift()
          this.vibrationChart.data.datasets[0].data.shift()
        }
        this.vibrationChart.update()
      }

      if (this.noiseChart) {
        const avgNoise = Object.values(this.blastData).reduce(
            (sum, zone) => sum + (zone.noiseLevel || 0), 0) / (Object.keys(this.blastData).length || 1)

        this.noiseChart.data.labels.push(timeString)
        this.noiseChart.data.datasets[0].data.push(avgNoise)

        if (this.noiseChart.data.labels.length > 10) {
          this.noiseChart.data.labels.shift()
          this.noiseChart.data.datasets[0].data.shift()
        }
        this.noiseChart.update()
      }
    },

    /**
     * 获取安全状态样式类
     */
    getSafetyClass(status) {
      switch (status) {
        case 'safe':
          return 'status-safe'
        case 'warning':
          return 'status-warning'
        case 'danger':
          return 'status-danger'
        default:
          return 'status-safe'
      }
    },

    /**
     * 格式化安全状态显示文本
     */
    formatSafetyStatus(status) {
      const statusMap = {
        'safe': '安全',
        'warning': '警告',
        'danger': '危险',
        'unknown': '未知'
      }
      return statusMap[status] || '安全'
    },

    /**
     * 格式化时间戳（兼容Java返回的ISO时间格式）
     */
    formatTimestamp(timestamp) {
      if (!timestamp) return '未知时间'
      try {
        // 处理Java LocalDateTime返回的格式（如：2025-12-12T14:23:45）
        const date = new Date(timestamp.replace('T', ' '))
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
      } catch (e) {
        return timestamp
      }
    }
  }
}
</script>

<style scoped>
.blast-monitoring {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;
}

.blast-monitoring h2 {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

/* 空数据提示 */
.empty-tip {
  text-align: center;
  padding: 3rem;
  color: #999;
  font-size: 1.2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.monitoring-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.blast-zone {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: all 0.3s ease;
}

.blast-zone:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.blast-zone h3 {
  color: #1890ff;
  margin-bottom: 1rem;
  font-size: 1.2rem;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.zone-metrics {
  margin-bottom: 1rem;
}

.metric-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.8rem;
}

.metric-item {
  flex: 1;
  margin: 0 0.5rem;
}

.metric-label {
  display: block;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.3rem;
}

.metric-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
}

.safety-status {
  padding: 0.8rem;
  border-radius: 6px;
  text-align: center;
  font-weight: bold;
  margin-bottom: 0.5rem;
  transition: all 0.3s ease;
}

.timestamp {
  font-size: 0.8rem;
  color: #999;
  text-align: right;
}

.visualization-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.chart-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  height: 400px; /* 固定图表高度，保证显示效果 */
}

/* 安全状态颜色 */
.status-safe {
  background-color: #e8f5e9;
  color: #52c41a;
}

.status-warning {
  background-color: #fff7e6;
  color: #faad14;
}

.status-danger {
  background-color: #fff1f0;
  color: #f5222d;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .monitoring-grid {
    grid-template-columns: 1fr;
  }

  .visualization-section {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 300px;
  }
}
</style>