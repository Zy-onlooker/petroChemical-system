<template>
  <div class="environmental-analysis">
    <!-- 标题区域优化：增加副标题+分隔线 -->
    <div class="page-header">
      <h2>环境影响分析</h2>
      <p class="page-subtitle">实时监测空气质量、水质、噪音、振动等核心环境指标</p>
      <div class="divider"></div>
    </div>

    <div class="analysis-cards">
      <div class="analysis-card">
        <h3 class="card-title">
          <i class="card-icon air-icon">🌬️</i>
          空气质量监测
        </h3>
        <!-- 加载状态补充（原有isLoading利用） -->
        <div v-if="isLoading" class="chart-loading">
          <div class="loading-spinner"></div>
          <p>加载空气质量数据...</p>
        </div>
        <div v-else class="air-quality-chart">
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
            <!-- 优化：改为徽章样式 -->
            <span class="status-badge" :class="getAirQualityClass(currentAQI)">
              {{ getAirQualityLevel(currentAQI) }}
            </span>
          </div>
        </div>
      </div>

      <div class="analysis-card">
        <h3 class="card-title">
          <i class="card-icon water-icon">💧</i>
          水质监测
        </h3>
        <div v-if="isLoading" class="chart-loading">
          <div class="loading-spinner"></div>
          <p>加载水质数据...</p>
        </div>
        <div v-else class="water-quality-metrics">
          <div class="metric-group">
            <div class="metric">
              <span class="metric-label">pH值</span>
              <span class="metric-value">{{ waterData.ph.toFixed(1) }}</span>
              <!-- 补充pH值状态提示 -->
              <span class="metric-tip" v-if="waterData.ph < 6 || waterData.ph > 8">⚠️ 超出正常范围(6-8)</span>
            </div>
            <div class="metric">
              <span class="metric-label">浊度</span>
              <span class="metric-value">{{ waterData.turbidity.toFixed(1) }} NTU</span>
              <span class="metric-tip" v-if="waterData.turbidity >= 2">⚠️ 浊度偏高</span>
            </div>
          </div>
          <div class="metric-group">
            <div class="metric">
              <span class="metric-label">电导率</span>
              <span class="metric-value">{{ waterData.conductivity.toFixed(0) }} μS/cm</span>
            </div>
            <div class="metric">
              <span class="metric-label">水质状态</span>
              <!-- 优化：改为徽章样式 -->
              <span class="status-badge" :class="getWaterQualityClass(waterData.ph, waterData.turbidity)">
                {{ getWaterQualityStatus(waterData.ph, waterData.turbidity) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="comprehensive-chart-card">
      <h3 class="card-title">
        <i class="card-icon comp-icon">📊</i>
        综合环境指标
      </h3>
      <div v-if="isLoading" class="chart-loading">
        <div class="loading-spinner"></div>
        <p>加载综合数据...</p>
      </div>
      <div v-else ref="comprehensiveContainer" class="comprehensive-chart">
        <canvas id="comprehensiveChart"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
// 1. 引入Chart.js并完全禁用插件/事件系统（功能不变）
import { Chart } from 'chart.js'

// 全局禁用Chart.js所有插件和事件（核心修复，保留）
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
    // 2. 延迟初始化，避免DOM未加载完成（功能不变）
    setTimeout(() => {
      this.initData()
      this.renderAllCharts()
      // 降低更新频率，避免递归压力（功能不变）
      this.updateTimer = setInterval(() => {
        if (!this.isLoading) this.initData()
      }, 10000)
    }, 1000)
  },
  beforeUnmount() {
    // 3. 彻底清理资源（功能不变）
    clearInterval(this.updateTimer)
    this.destroyChart('airQualityChart')
    this.destroyChart('comprehensiveChart')
  },
  methods: {
    // 销毁指定图表（避免实例残留，功能不变）
    destroyChart(chartId) {
      const chartInstance = Chart.getChart(chartId)
      if (chartInstance) {
        chartInstance.destroy()
      }
    },
    // 初始化/更新数据（简化逻辑，避免reduce/递归操作，功能不变）
    async initData() {
      this.isLoading = true
      try {
        // 模拟接口请求（替换为真实接口，保留极简逻辑，功能不变）
        const mockSensorData = { sensor_1: { data: { ph: 7.2, turbidity: 1.5, conductivity: 550 } } }
        const mockBlastData = { blast_zone_1: { airQuality: Math.floor(Math.random() * 50) + 30, noiseLevel: Math.floor(Math.random() * 20) + 70, vibrationLevel: Math.floor(Math.random() * 20) + 65 } }

        // 直接赋值，避免响应式递归（功能不变）
        this.blastData = mockBlastData
        const aqi = mockBlastData.blast_zone_1.airQuality
        this.currentAQI = Math.max(0, Math.min(100, aqi))

        // 更新历史数据（纯数组操作，无响应式，功能不变）
        this.airQualityHistory.push(this.currentAQI)
        if (this.airQualityHistory.length > 10) this.airQualityHistory.shift()

        // 更新水质数据（功能不变）
        const sensorData = mockSensorData // 真实环境替换为await fetch结果
        const firstSensor = sensorData[Object.keys(sensorData)[0]] || {}
        const sensorDetail = firstSensor.data || {}
        this.waterData = {
          ph: typeof sensorDetail.ph === 'number' ? sensorDetail.ph : 7.0,
          turbidity: typeof sensorDetail.turbidity === 'number' ? sensorDetail.turbidity : 1.0,
          conductivity: typeof sensorDetail.conductivity === 'number' ? sensorDetail.conductivity : 500
        }

        // 重新渲染图表（销毁旧的，创建新的，避免配置递归，功能不变）
        this.renderAllCharts()
      } catch (err) {
        console.error('数据更新失败:', err)
      } finally {
        this.isLoading = false
      }
    },
    // 渲染所有图表（每次重建，避免update导致的递归，功能不变）
    renderAllCharts() {
      this.renderAirQualityChart()
      this.renderComprehensiveChart()
    },
    // 渲染空气质量折线图（极简配置，禁用所有插件/事件，仅优化配色）
    renderAirQualityChart() {
      // 先销毁旧图表（功能不变）
      this.destroyChart('airQualityChart')

      const ctx = document.getElementById('airQualityChart')
      if (!ctx) return

      new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['-9s', '-8s', '-7s', '-6s', '-5s', '-4s', '-3s', '-2s', '-1s', 'now'],
          datasets: [{
            label: 'AQI指数',
            data: [...this.airQualityHistory], // 浅拷贝避免引用（功能不变）
            borderColor: '#2ecc71', // 优化：更柔和的绿色
            backgroundColor: 'rgba(46, 204, 113, 0.15)', // 优化：浅绿背景
            tension: 0.4, // 优化：曲线更顺滑
            fill: true,
            borderWidth: 2,
            pointBackgroundColor: '#2ecc71', // 新增：数据点颜色
            pointRadius: 3 // 新增：数据点大小
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {}, // 禁用所有插件（功能不变）
          events: [], // 禁用所有事件（功能不变）
          animation: { duration: 300 }, // 优化：柔和动画
          scales: {
            y: {
              min: 0,
              max: 100,
              ticks: { stepSize: 20, font: { size: 11, family: 'Microsoft Yahei' } },
              grid: { display: true, color: 'rgba(0,0,0,0.05)' } // 优化：网格线更淡
            },
            x: {
              ticks: { display: true, font: { size: 11, family: 'Microsoft Yahei' } },
              grid: { display: false }
            }
          }
        }
      })
    },
    // 渲染综合柱状图（极简配置，仅优化配色）
    renderComprehensiveChart() {
      // 先销毁旧图表（功能不变）
      this.destroyChart('comprehensiveChart')

      const ctx = document.getElementById('comprehensiveChart')
      if (!ctx) return

      // 简化评分计算，避免复杂逻辑（功能不变）
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
            // 优化：更协调的配色
            backgroundColor: [
              'rgba(46, 204, 113, 0.7)',
              'rgba(52, 152, 219, 0.7)',
              'rgba(231, 76, 60, 0.7)',
              'rgba(155, 89, 182, 0.7)'
            ],
            borderColor: [
              'rgba(46, 204, 113, 1)',
              'rgba(52, 152, 219, 1)',
              'rgba(231, 76, 60, 1)',
              'rgba(155, 89, 182, 1)'
            ],
            borderWidth: 1,
            borderRadius: 4 // 新增：柱状图圆角
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {}, // 禁用所有插件（功能不变）
          events: [], // 禁用所有事件（功能不变）
          animation: { duration: 300 }, // 优化：柔和动画
          scales: {
            y: {
              min: 0,
              max: 100,
              ticks: { stepSize: 20, font: { size: 11, family: 'Microsoft Yahei' } },
              grid: { display: true, color: 'rgba(0,0,0,0.05)' } // 优化：网格线更淡
            },
            x: {
              ticks: { font: { size: 11, family: 'Microsoft Yahei' } },
              grid: { display: false }
            }
          }
        }
      })
    },
    // 简化水质评分计算（功能不变）
    calcWaterScore() {
      let score = 100
      if (this.waterData.ph < 6 || this.waterData.ph > 8) score -= 20
      if (this.waterData.turbidity > 5) score -= 30
      if (this.waterData.conductivity > 800) score -= 20
      return Math.max(0, Math.min(100, score))
    },
    // 空气质量等级（返回类名不变，功能不变）
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
    // 水质等级（返回类名不变，功能不变）
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
/* 全局样式优化：统一字体、背景、间距 */
.environmental-analysis {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1.5rem;
  font-family: 'Microsoft Yahei', 'PingFang SC', sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* 页面头部优化 */
.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.page-header h2 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 600;
}

.page-subtitle {
  color: #7f8c8d;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.divider {
  width: 80px;
  height: 3px;
  background-color: #2ecc71;
  margin: 0 auto;
  border-radius: 3px;
}

/* 卡片容器优化 */
.analysis-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1.8rem;
  margin-bottom: 2rem;
}

/* 卡片样式优化：增加立体感、hover动效 */
.analysis-card, .comprehensive-chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 1.8rem;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.analysis-card:hover, .comprehensive-chart-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

/* 卡片标题优化：增加图标、统一样式 */
.card-title {
  color: #2c3e50;
  margin-bottom: 1.2rem;
  font-size: 1.3rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.card-icon {
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(46, 204, 113, 0.1);
}

.air-icon {
  background-color: rgba(46, 204, 113, 0.1);
  color: #2ecc71;
}

.water-icon {
  background-color: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.comp-icon {
  background-color: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}

/* 图表容器优化：强制高度、圆角 */
.air-quality-chart {
  height: 220px;
  width: 100%;
  margin-bottom: 1.2rem;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fafafa;
  padding: 0.5rem;
}

.air-quality-chart canvas {
  width: 100% !important;
  height: 100% !important;
}

/* 统计项优化：间距、背景 */
.air-quality-stats {
  display: flex;
  gap: 1.2rem;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  border-radius: 8px;
  background-color: #f5f7fa;
  flex: 1;
  transition: all 0.2s ease;
}

.stat-item:hover {
  background-color: #eef2f7;
}

.stat-label {
  display: block;
  font-size: 0.85rem;
  color: #7f8c8d;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
}

/* 状态徽章样式：替换单纯文字颜色，更醒目 */
.status-badge {
  display: inline-block;
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 1.1rem;
  font-weight: 600;
  color: white;
}

/* 空气质量徽章配色优化 */
.air-quality-excellent { background-color: #2ecc71; }
.air-quality-good { background-color: #27ae60; }
.air-quality-moderate { background-color: #f39c12; }
.air-quality-unhealthy { background-color: #e74c3c; }
.air-quality-hazardous { background-color: #c0392b; }

/* 水质徽章配色优化 */
.water-quality-good { background-color: #3498db; }
.water-quality-moderate { background-color: #f39c12; }
.water-quality-poor { background-color: #e74c3c; }

/* 水质指标优化：间距、提示 */
.water-quality-metrics {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.metric-group {
  display: flex;
  gap: 1.2rem;
}

.metric {
  flex: 1;
  padding: 1rem;
  background-color: #f5f7fa;
  border-radius: 8px;
  text-align: center;
  transition: all 0.2s ease;
  position: relative;
}

.metric:hover {
  background-color: #eef2f7;
}

.metric-label {
  display: block;
  font-size: 0.85rem;
  color: #7f8c8d;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.metric-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  display: block;
}

/* 指标提示：异常时显示 */
.metric-tip {
  font-size: 0.75rem;
  color: #e74c3c;
  margin-top: 0.3rem;
  display: block;
}

/* 综合图表卡片优化 */
.comprehensive-chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 1.8rem;
  margin-top: 1rem;
  height: 420px;
  transition: all 0.3s ease;
}

.comprehensive-chart {
  width: 100% !important;
  height: calc(100% - 40px) !important;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fafafa;
  padding: 0.5rem;
}

.comprehensive-chart canvas {
  width: 100% !important;
  height: 100% !important;
}

/* 加载状态样式 */
.chart-loading {
  height: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  color: #7f8c8d;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #2ecc71;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式优化：更精细的适配 */
@media (max-width: 768px) {
  .environmental-analysis {
    padding: 1rem;
  }

  .analysis-cards {
    grid-template-columns: 1fr;
    gap: 1.2rem;
  }

  .metric-group {
    flex-direction: column;
    gap: 0.8rem;
  }

  .comprehensive-chart-card {
    height: 350px;
    padding: 1.2rem;
  }

  .air-quality-chart {
    height: 200px;
  }

  .page-header h2 {
    font-size: 1.6rem;
  }

  .card-title {
    font-size: 1.2rem;
  }
}

@media (max-width: 480px) {
  .air-quality-stats {
    flex-direction: column;
    gap: 0.8rem;
  }

  .comprehensive-chart-card {
    height: 300px;
  }

  .analysis-card, .comprehensive-chart-card {
    padding: 1.2rem;
  }
}
</style>