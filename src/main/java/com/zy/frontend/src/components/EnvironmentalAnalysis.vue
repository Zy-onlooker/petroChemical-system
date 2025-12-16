<template>
  <div class="environmental-analysis">
    <h2>环境影响分析</h2>

    <div class="analysis-cards">
      <div class="analysis-card">
        <h3>空气质量监测</h3>
        <div class="air-quality-chart">
          <!-- 每次重建图表前清空容器 -->
          <div ref="airQualityContainer">
            <canvas id="airQualityChart"></canvas>
          </div>
        </div>
        <div class="air-quality-stats">
          <div class="stat-item">
            <span class="stat-label">当前AQI</span>
            <span class="stat-value">{{ currentAQI.toFixed(0) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">空气质量</span>
            <span class="stat-value" :class="getAirQualityClass(currentAQI)">
              {{ getAirQualityLevel(currentAQI) }}
            </span>
          </div>
        </div>
      </div>

      <div class="analysis-card">
        <h3>水质监测</h3>
        <div class="water-quality-metrics">
          <div class="metric-group">
            <div class="metric">
              <span class="metric-label">pH值</span>
              <span class="metric-value">{{ waterData.ph.toFixed(1) }}</span>
            </div>
            <div class="metric">
              <span class="metric-label">浊度</span>
              <span class="metric-value">{{ waterData.turbidity.toFixed(1) }} NTU</span>
            </div>
          </div>
          <div class="metric-group">
            <div class="metric">
              <span class="metric-label">电导率</span>
              <span class="metric-value">{{ waterData.conductivity.toFixed(0) }} μS/cm</span>
            </div>
            <div class="metric">
              <span class="metric-label">水质状态</span>
              <span class="metric-value" :class="getWaterQualityClass(waterData.ph, waterData.turbidity)">
                {{ getWaterQualityStatus(waterData.ph, waterData.turbidity) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="comprehensive-chart">
      <h3>综合环境指标</h3>
      <div ref="comprehensiveContainer">
        <canvas id="comprehensiveChart"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
// 1. 引入Chart.js并完全禁用插件/事件系统
import { Chart } from 'chart.js'

// 全局禁用Chart.js所有插件和事件（核心修复）
Chart.defaults.plugins = {}
Chart.defaults.events = [] // 禁用所有事件，避免eventFilter报错
Chart.defaults.interaction = { mode: 'none' }

export default {
  name: 'EnvironmentalAnalysis',
  data() {
    return {
      currentAQI: 50,
      waterData: { ph: 7.0, turbidity: 1.0, conductivity: 500 },
      blastData: { blast_zone_1: { airQuality: 50, noiseLevel: 80, vibrationLevel: 75 } },
      airQualityHistory: Array(10).fill(50), // 纯普通数组
      updateTimer: null,
      isLoading: false
    }
  },
  mounted() {
    // 2. 延迟初始化，避免DOM未加载完成
    setTimeout(() => {
      this.initData()
      this.renderAllCharts()
      // 降低更新频率，避免递归压力
      this.updateTimer = setInterval(() => {
        if (!this.isLoading) this.initData()
      }, 10000)
    }, 1000)
  },
  beforeUnmount() {
    // 3. 彻底清理资源
    clearInterval(this.updateTimer)
    this.destroyChart('airQualityChart')
    this.destroyChart('comprehensiveChart')
  },
  methods: {
    // 销毁指定图表（避免实例残留）
    destroyChart(chartId) {
      const chartInstance = Chart.getChart(chartId)
      if (chartInstance) {
        chartInstance.destroy()
      }
    },
    // 初始化/更新数据（简化逻辑，避免reduce/递归操作）
    async initData() {
      this.isLoading = true
      try {
        // 模拟接口请求（替换为真实接口，保留极简逻辑）
        const mockSensorData = { sensor_1: { data: { ph: 7.2, turbidity: 1.5, conductivity: 550 } } }
        const mockBlastData = { blast_zone_1: { airQuality: Math.floor(Math.random() * 50) + 30, noiseLevel: Math.floor(Math.random() * 20) + 70, vibrationLevel: Math.floor(Math.random() * 20) + 65 } }

        // 直接赋值，避免响应式递归
        this.blastData = mockBlastData
        const aqi = mockBlastData.blast_zone_1.airQuality
        this.currentAQI = Math.max(0, Math.min(100, aqi))

        // 更新历史数据（纯数组操作，无响应式）
        this.airQualityHistory.push(this.currentAQI)
        if (this.airQualityHistory.length > 10) this.airQualityHistory.shift()

        // 更新水质数据
        const sensorData = mockSensorData // 真实环境替换为await fetch结果
        const firstSensor = sensorData[Object.keys(sensorData)[0]] || {}
        const sensorDetail = firstSensor.data || {}
        this.waterData = {
          ph: typeof sensorDetail.ph === 'number' ? sensorDetail.ph : 7.0,
          turbidity: typeof sensorDetail.turbidity === 'number' ? sensorDetail.turbidity : 1.0,
          conductivity: typeof sensorDetail.conductivity === 'number' ? sensorDetail.conductivity : 500
        }

        // 重新渲染图表（销毁旧的，创建新的，避免配置递归）
        this.renderAllCharts()
      } catch (err) {
        console.error('数据更新失败:', err)
      } finally {
        this.isLoading = false
      }
    },
    // 渲染所有图表（每次重建，避免update导致的递归）
    renderAllCharts() {
      this.renderAirQualityChart()
      this.renderComprehensiveChart()
    },
    // 渲染空气质量折线图（极简配置，禁用所有插件/事件）
    renderAirQualityChart() {
      // 先销毁旧图表
      this.destroyChart('airQualityChart')

      const ctx = document.getElementById('airQualityChart')
      if (!ctx) return

      new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['-9s', '-8s', '-7s', '-6s', '-5s', '-4s', '-3s', '-2s', '-1s', 'now'],
          datasets: [{
            label: 'AQI指数',
            data: [...this.airQualityHistory], // 浅拷贝避免引用
            borderColor: '#52c41a',
            backgroundColor: 'rgba(82, 196, 26, 0.1)',
            tension: 0.3,
            fill: true,
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {}, // 禁用所有插件
          events: [], // 禁用所有事件
          animation: false,
          scales: {
            y: { min: 0, max: 100, ticks: { stepSize: 20 }, grid: { display: true } },
            x: { ticks: { display: true }, grid: { display: false } }
          }
        }
      })
    },
    // 渲染综合柱状图（极简配置）
    renderComprehensiveChart() {
      // 先销毁旧图表
      this.destroyChart('comprehensiveChart')

      const ctx = document.getElementById('comprehensiveChart')
      if (!ctx) return

      // 简化评分计算，避免复杂逻辑
      const waterScore = this.calcWaterScore()
      const noiseScore = this.blastData.blast_zone_1.noiseLevel || 80
      const vibrationScore = this.blastData.blast_zone_1.vibrationLevel || 75

      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['空气质量', '水质', '噪音', '振动'],
          datasets: [{
            label: '环境指标',
            data: [this.currentAQI, waterScore, noiseScore, vibrationScore],
            backgroundColor: [
              'rgba(82, 196, 26, 0.7)',
              'rgba(24, 144, 255, 0.7)',
              'rgba(250, 84, 28, 0.7)',
              'rgba(114, 46, 209, 0.7)'
            ],
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {}, // 禁用所有插件
          events: [], // 禁用所有事件
          animation: false,
          scales: {
            y: { min: 0, max: 100, ticks: { stepSize: 20 }, grid: { display: true } },
            x: { grid: { display: false } }
          }
        }
      })
    },
    // 简化水质评分计算
    calcWaterScore() {
      let score = 100
      if (this.waterData.ph < 6 || this.waterData.ph > 8) score -= 20
      if (this.waterData.turbidity > 5) score -= 30
      if (this.waterData.conductivity > 800) score -= 20
      return Math.max(0, Math.min(100, score))
    },
    // 空气质量等级
    getAirQualityClass(aqi) {
      if (aqi < 50) return 'air-quality-excellent'
      if (aqi < 100) return 'air-quality-good'
      if (aqi < 150) return 'air-quality-moderate'
      if (aqi < 200) return 'air-quality-unhealthy'
      return 'air-quality-hazardous'
    },
    getAirQualityLevel(aqi) {
      if (aqi < 50) return '优'
      if (aqi < 100) return '良'
      if (aqi < 150) return '轻度污染'
      if (aqi < 200) return '中度污染'
      return '重度污染'
    },
    // 水质等级
    getWaterQualityClass(ph, turbidity) {
      if (ph >= 6 && ph <= 8 && turbidity < 2) return 'water-quality-good'
      if ((ph < 6 || ph > 8) && turbidity < 5) return 'water-quality-moderate'
      return 'water-quality-poor'
    },
    getWaterQualityStatus(ph, turbidity) {
      if (ph >= 6 && ph <= 8 && turbidity < 2) return '优'
      if ((ph < 6 || ph > 8) && turbidity < 5) return '良'
      return '差'
    }
  }
}
</script>

<style scoped>
.environmental-analysis {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
}

.environmental-analysis h2 {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

.analysis-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.analysis-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 1.5rem;
}

.analysis-card h3 {
  color: #52c41a;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

/* 关键：强制图表容器高度，确保canvas渲染 */
.air-quality-chart {
  height: 200px;
  width: 100%;
  margin-bottom: 1rem;
}
.air-quality-chart canvas {
  width: 100% !important;
  height: 100% !important;
}

.air-quality-stats {
  display: flex;
  gap: 1rem;
}

.stat-item {
  text-align: center;
  padding: 0.8rem;
  border-radius: 6px;
  background-color: #f5f7fa;
  flex: 1;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.3rem;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: bold;
}

.water-quality-metrics {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.metric-group {
  display: flex;
  gap: 1rem;
}

.metric {
  flex: 1;
  padding: 0.8rem;
  background-color: #f5f7fa;
  border-radius: 6px;
  text-align: center;
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
}

.comprehensive-chart {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 1.5rem;
  margin-top: 1rem;
  height: 400px;
}
.comprehensive-chart canvas {
  width: 100% !important;
  height: 100% !important;
}

.comprehensive-chart h3 {
  color: #1890ff;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

/* 空气质量样式 */
.air-quality-excellent { color: #52c41a; }
.air-quality-good { color: #73d13d; }
.air-quality-moderate { color: #faad14; }
.air-quality-unhealthy { color: #ff7a45; }
.air-quality-hazardous { color: #f5222d; }

/* 水质样式 */
.water-quality-good { color: #52c41a; }
.water-quality-moderate { color: #faad14; }
.water-quality-poor { color: #f5222d; }

/* 响应式 */
@media (max-width: 768px) {
  .analysis-cards { grid-template-columns: 1fr; }
  .metric-group { flex-direction: column; }
  .comprehensive-chart { height: 300px; }
}
</style>