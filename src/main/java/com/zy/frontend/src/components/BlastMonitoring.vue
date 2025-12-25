<template>
  <div class="blast-monitoring">
    <!-- 页面头部：标题 + 刷新按钮 -->
    <div class="page-header">
      <h2 class="page-title">爆破安全监控系统</h2>
      <button
          class="refresh-btn"
          @click="fetchBlastData"
          :disabled="isLoading"
      >
        <i class="icon-refresh"></i>
        {{ isLoading ? '刷新中...' : '手动刷新' }}
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">正在获取监控数据...</p>
    </div>

    <!-- 空数据状态（即使无数据也显示3个空卡片） -->
    <div v-else class="content-wrapper">
      <!-- 区域卡片网格（固定渲染3个区域） -->
      <div class="monitoring-grid">
        <!-- 固定渲染blast_zone_1/2/3（对应后端ID） -->
        <div
            class="blast-zone-card"
            v-for="zoneId in validZoneIds"
            :key="zoneId"
            :class="{
              'card-warning': blastData[zoneId]?.safetyStatus === '警告',
              'card-alarm': blastData[zoneId]?.safetyStatus === '危险'
            }"
        >
          <!-- 卡片头部：区域名称 + 报警标识 -->
          <div class="card-header">
            <h3 class="zone-name">{{ zoneNameMap[zoneId] }}</h3>
            <span v-if="blastData[zoneId]?.safetyStatus === '警告'" class="warning-badge">警告</span>
            <span v-if="blastData[zoneId]?.safetyStatus === '危险'" class="alarm-badge">报警</span>
          </div>

          <!-- 核心指标区域 -->
          <div class="metrics-grid">
            <!-- 振动水平 -->
            <div class="metric-item">
              <span class="metric-label">振动水平</span>
              <span class="metric-value"
                    :class="{
                  'warning-value': blastData[zoneId]?.vibrationLevel > 85 && blastData[zoneId]?.vibrationLevel <= 95,
                  'danger-value': blastData[zoneId]?.vibrationLevel > 95
                }">
                {{ (blastData[zoneId]?.vibrationLevel || 0).toFixed(1) }} mm/s
                <span class="threshold-tip">(警告: >85 | 危险: >95)</span>
              </span>
            </div>

            <!-- 噪音水平 -->
            <div class="metric-item">
              <span class="metric-label">噪音水平</span>
              <span class="metric-value"
                    :class="{
                  'warning-value': blastData[zoneId]?.noiseLevel > 85 && blastData[zoneId]?.noiseLevel <= 95,
                  'danger-value': blastData[zoneId]?.noiseLevel > 95
                }">
                {{ (blastData[zoneId]?.noiseLevel || 0).toFixed(1) }} dB
                <span class="threshold-tip">(警告: >85 | 危险: >95)</span>
              </span>
            </div>

            <!-- 空气质量 -->
            <div class="metric-item">
              <span class="metric-label">空气质量</span>
              <span class="metric-value"
                    :class="{
                  'warning-value': blastData[zoneId]?.airQuality > 80 && blastData[zoneId]?.airQuality <= 90,
                  'danger-value': blastData[zoneId]?.airQuality > 90
                }">
                {{ (blastData[zoneId]?.airQuality || 0).toFixed(0) }} AQI
                <span class="threshold-tip">(警告: >80 | 危险: >90)</span>
              </span>
            </div>

            <!-- 人员数量 -->
            <div class="metric-item">
              <span class="metric-label">人员数量</span>
              <span class="metric-value"
                    :class="{
                  'warning-value': blastData[zoneId]?.personnelCount > 0 && blastData[zoneId]?.distanceToBlast <= 15,
                  'danger-value': blastData[zoneId]?.personnelCount > 0 && blastData[zoneId]?.distanceToBlast <= 5
                }">
                {{ blastData[zoneId]?.personnelCount || 0 }} 人
                <span class="threshold-tip">(警告: ≤15人 | 危险: ≤5人)</span>
              </span>
            </div>

            <!-- 可燃气体浓度 -->
            <div class="metric-item">
              <span class="metric-label">可燃气体浓度</span>
              <span
                  class="metric-value"
                  :class="{
                    'warning-value': (blastData[zoneId]?.explosiveGas || 0) > 3 && (blastData[zoneId]?.explosiveGas || 0) <= 5,
                    'danger-value': (blastData[zoneId]?.explosiveGas || 0) > 5
                  }">
                {{ (blastData[zoneId]?.explosiveGas || 0).toFixed(1) }} ppm
                <span class="threshold-tip">(警告: 3-5 | 危险: >5)</span>
              </span>
            </div>

            <!-- 距离爆破点 -->
            <div class="metric-item">
              <span class="metric-label">距离爆破点</span>
              <span
                  class="metric-value"
                  :class="{
                    'warning-value': (blastData[zoneId]?.distanceToBlast || 0) <= 15 && (blastData[zoneId]?.distanceToBlast || 0) > 5,
                    'danger-value': (blastData[zoneId]?.distanceToBlast || 0) <= 5
                  }">
                {{ (blastData[zoneId]?.distanceToBlast || 0).toFixed(0) }} 米
                <span class="threshold-tip">(警告: ≤15米 | 危险: ≤5米)</span>
              </span>
            </div>
          </div>

          <!-- 安全状态 -->
          <div class="safety-status-wrapper">
            <div class="safety-status" :class="getSafetyClass(blastData[zoneId]?.safetyStatus)">
              <span class="status-label">安全状态：</span>
              <span class="status-text">{{ formatSafetyStatus(blastData[zoneId]?.safetyStatus) }}</span>
            </div>
          </div>

          <!-- 更新时间 -->
          <div class="update-time">
            最后更新：{{ formatTimestamp(blastData[zoneId]?.timestamp) }}
          </div>
        </div>
      </div>

      <!-- 报警记录区域 -->
      <div v-if="alarmRecords.length > 0" class="alarm-records">
        <h3 class="alarm-title">⚠️ 异常记录</h3>
        <div class="alarm-list">
          <div class="alarm-item" v-for="(record, index) in alarmRecords" :key="index"
               :class="{ 'warning-item': record.level === '警告', 'danger-item': record.level === '危险' }">
            <span class="alarm-time">{{ record.time }}</span>
            <span class="alarm-zone">{{ record.zoneName }}</span>
            <span class="alarm-indicator">{{ record.indicator }}</span>
            <span class="alarm-level">{{ record.level }}</span>
            <span class="alarm-reason">{{ record.reason }}</span>
            <span class="alarm-value">数值：{{ record.value }}</span>
          </div>
        </div>
      </div>

    </div>

    <!-- 错误提示（全局） -->
    <div v-if="errorMsg" class="error-container">
      <div class="error-icon">⚠️</div>
      <p class="error-text">{{ errorMsg }}</p>
      <button class="error-retry-btn" @click="fetchBlastData">重试</button>
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
      blastData: {},        // 监控数据
      vibrationChart: null, // 振动图表实例
      noiseChart: null,     // 噪音图表实例
      updateInterval: null, // 定时刷新定时器
      isLoading: false,     // 加载状态
      errorMsg: '',         // 错误提示信息
      alarmRecords: [],     // 报警记录
      // 修正：匹配后端的区域ID
      validZoneIds: ['blast_zone_1', 'blast_zone_2', 'blast_zone_3'],
      // 区域名称映射
      zoneNameMap: {
        blast_zone_1: '爆破区域一',
        blast_zone_2: '爆破区域二',
        blast_zone_3: '爆破区域三'
      },
      // 图表颜色配置
      chartColors: {
        blast_zone_1: {border: '#1890ff', background: 'rgba(24, 144, 255, 0.1)'},
        blast_zone_2: {border: '#52c41a', background: 'rgba(82, 196, 26, 0.1)'},
        blast_zone_3: {border: '#fa541c', background: 'rgba(250, 84, 28, 0.1)'}
      },
      // 🌟 调整后的校验规则（分级判定+放宽阈值）
      validationRules: {
        vibrationLevel: {
          warning: {min: 85, max: 95, message: '振动水平偏高（85-95mm/s）'},
          danger: {min: 95, message: '振动水平严重超标（>95mm/s）'}
        },
        noiseLevel: {
          warning: {min: 85, max: 95, message: '噪音水平偏高（85-95dB）'},
          danger: {min: 95, message: '噪音水平严重超标（>95dB）'}
        },
        airQuality: {
          warning: {min: 80, max: 90, message: '空气质量偏差（80-90AQI）'},
          danger: {min: 90, message: '空气质量严重超标（>90AQI）'}
        },
        explosiveGas: {
          warning: {min: 3, max: 5, message: '可燃气体浓度偏高（3-5ppm）'},
          danger: {min: 5, message: '可燃气体浓度严重超标（>5ppm）'}
        },
        distanceToBlast: {
          warning: {max: 15, min: 5, message: '距离爆破点较近（5-15米）'},
          danger: {max: 5, message: '距离爆破点极近（≤5米）'}
        },
        personnelCount: {
          warning: {
            condition: (data) => data.personnelCount > 0 && data.distanceToBlast <= 15 && data.distanceToBlast > 5,
            message: '人员进入较危险区域（5-15米）'
          },
          danger: {
            condition: (data) => data.personnelCount > 0 && data.distanceToBlast <= 5,
            message: '人员进入极度危险区域（≤5米）'
          }
        }
      }
    }
  },
  computed: {
    // 判断是否有有效数据
    hasValidData() {
      return this.validZoneIds.some(zoneId => {
        const zone = this.blastData[zoneId] || {}
        return zone.vibrationLevel || zone.noiseLevel || zone.airQuality
      })
    }
  },
  mounted() {
    // 初始化数据请求
    this.fetchBlastData()
    // 定时刷新（5秒）
    this.updateInterval = setInterval(this.fetchBlastData, 10000)
  },
  beforeUnmount() {
    // 清理资源
    if (this.updateInterval) clearInterval(this.updateInterval)
    this.destroyChart(this.vibrationChart)
    this.destroyChart(this.noiseChart)
  },
  methods: {
    /**
     * 销毁图表实例
     */
    destroyChart(chartInstance) {
      if (chartInstance) {
        chartInstance.destroy()
        chartInstance = null
      }
    },

    /**
     * 🌟 生成合理的随机数据（大部分安全，少量警告，极少危险）
     */
    generateRandomBlastData(zoneId) {
      const random = Math.random;
      // 1. 先确定整体状态（90%安全，7%警告，3%危险）
      const statusRand = random();
      let overallStatus = '安全';
      if (statusRand > 0.9 && statusRand <= 0.97) {
        overallStatus = '警告';
      } else if (statusRand > 0.97) {
        overallStatus = '危险';
      }

      // 2. 定义各指标基础值（默认安全）
      let vibrationLevel = 70 + random() * 15; // 安全：70-85
      let noiseLevel = 60 + random() * 25; // 安全：60-85
      let airQuality = 50 + random() * 30; // 安全：50-80
      let explosiveGas = random() * 3; // 安全：0-3（保留70%<3的逻辑，整体状态优先）
      let distanceToBlast = 15 + random() * 85; // 安全：15-100
      let personnelCount = 0; // 安全：默认无人

      // 3. 根据整体状态调整指标值
      if (overallStatus === '警告') {
        // 随机选1个指标设为警告（无危险指标）
        const warnIndex = Math.floor(random() * 6); // 0-5对应6个指标
        switch (warnIndex) {
          case 0: // 振动水平警告：85-95
            vibrationLevel = 85 + random() * 10;
            break;
          case 1: // 噪音水平警告：85-95
            noiseLevel = 85 + random() * 10;
            break;
          case 2: // 空气质量警告：80-90
            airQuality = 80 + random() * 10;
            break;
          case 3: // 可燃气体警告：3-5
            explosiveGas = 3 + random() * 2;
            break;
          case 4: // 距离警告：5-15
            distanceToBlast = 5 + random() * 10;
            break;
          case 5: // 人员+距离警告：有人且距离5-15
            personnelCount = 1;
            distanceToBlast = 5 + random() * 10;
            break;
        }
      } else if (overallStatus === '危险') {
        // 随机选1个指标设为危险
        const dangerIndex = Math.floor(random() * 6);
        switch (dangerIndex) {
          case 0: // 振动水平危险：>95
            vibrationLevel = 95 + random() * 5;
            break;
          case 1: // 噪音水平危险：>95
            noiseLevel = 95 + random() * 5;
            break;
          case 2: // 空气质量危险：>90
            airQuality = 90 + random() * 5;
            break;
          case 3: // 可燃气体危险：>5
            explosiveGas = 5 + random() * 1;
            break;
          case 4: // 距离危险：≤5
            distanceToBlast = 0 + random() * 5;
            break;
          case 5: // 人员+距离危险：有人且距离≤5
            personnelCount = 1;
            distanceToBlast = 0 + random() * 5;
            break;
        }
      }

      return {
        timestamp: new Date().toISOString().replace('T', ' ').split('.')[0],
        vibrationLevel,
        noiseLevel,
        airQuality,
        personnelCount,
        explosiveGas,
        distanceToBlast,
        safetyStatus: '安全' // 初始值，最终由validateData判定（和原逻辑一致）
      };
    },


    /**
     * 🌟 分级校验数据（安全/警告/危险）
     */
    validateData(zoneId, data) {
      const zoneName = this.zoneNameMap[zoneId]
      let status = '安全' // 默认安全
      const now = new Date().toLocaleString('zh-CN')
      const dangerIndicators = [] // 危险指标
      const warningIndicators = [] // 警告指标

      // 逐个校验指标
      Object.keys(this.validationRules).forEach(ruleKey => {
        const rule = this.validationRules[ruleKey]
        const value = data[ruleKey]

        // 数值型指标校验（振动/噪音/空气质量/气体/距离）
        if (rule.warning && typeof value === 'number') {
          // 危险判定
          if (rule.danger) {
            if (rule.danger.min && value > rule.danger.min) {
              dangerIndicators.push(ruleKey)
              this.alarmRecords.unshift({
                time: now,
                zoneName,
                indicator: this.getIndicatorName(ruleKey),
                level: '危险',
                reason: rule.danger.message,
                value: value.toFixed(1)
              })
            } else if (rule.danger.max && value < rule.danger.max) {
              dangerIndicators.push(ruleKey)
              this.alarmRecords.unshift({
                time: now,
                zoneName,
                indicator: this.getIndicatorName(ruleKey),
                level: '危险',
                reason: rule.danger.message,
                value: value.toFixed(0)
              })
            }
          }

          // 警告判定（未触发危险时才判定警告）
          if (status !== '危险' && rule.warning) {
            const warnRule = rule.warning
            if (warnRule.min && warnRule.max && value > warnRule.min && value <= warnRule.max) {
              warningIndicators.push(ruleKey)
              this.alarmRecords.unshift({
                time: now,
                zoneName,
                indicator: this.getIndicatorName(ruleKey),
                level: '警告',
                reason: warnRule.message,
                value: value.toFixed(1)
              })
            } else if (warnRule.max && !warnRule.min && value < warnRule.max) {
              warningIndicators.push(ruleKey)
              this.alarmRecords.unshift({
                time: now,
                zoneName,
                indicator: this.getIndicatorName(ruleKey),
                level: '警告',
                reason: warnRule.message,
                value: value.toFixed(0)
              })
            }
          }
        }

        // 复合条件校验（人员+距离）
        if (rule.warning?.condition && rule.warning.condition(data)) {
          warningIndicators.push(ruleKey)
          this.alarmRecords.unshift({
            time: now,
            zoneName,
            indicator: this.getIndicatorName(ruleKey),
            level: '警告',
            reason: rule.warning.message,
            value: data.personnelCount + '人/' + data.distanceToBlast + '米'
          })
        } else if (rule.danger?.condition && rule.danger.condition(data)) {
          dangerIndicators.push(ruleKey)
          this.alarmRecords.unshift({
            time: now,
            zoneName,
            indicator: this.getIndicatorName(ruleKey),
            level: '危险',
            reason: rule.danger.message,
            value: data.personnelCount + '人/' + data.distanceToBlast + '米'
          })
        }
      })

      // 最终状态判定：有危险指标→危险；有警告指标→警告；否则安全
      if (dangerIndicators.length > 0) {
        status = '危险'
      } else if (warningIndicators.length > 0) {
        status = '警告'
      }

      // 限制记录数量（最多15条）
      if (this.alarmRecords.length > 15) {
        this.alarmRecords = this.alarmRecords.slice(0, 15)
      }

      // 更新安全状态
      data.safetyStatus = status
      return data
    },

    /**
     * 获取指标中文名称
     */
    getIndicatorName(key) {
      const nameMap = {
        vibrationLevel: '振动水平',
        noiseLevel: '噪音水平',
        airQuality: '空气质量',
        explosiveGas: '可燃气体浓度',
        distanceToBlast: '距离爆破点',
        personnelCount: '人员数量'
      }
      return nameMap[key] || key
    },

    /**
     * 获取爆破监控数据
     */
    async fetchBlastData() {
      // 重置错误信息
      this.errorMsg = ''
      // 防止重复请求
      if (this.isLoading) return

      this.isLoading = true
      try {
        // 5秒超时控制
        const controller = new AbortController()
        const timeoutId = setTimeout(() => controller.abort(), 10000)

        const response = await fetch('/api/blast-data', {
          method: 'GET',
          signal: controller.signal,
          headers: {
            'Content-Type': 'application/json'
          }
        })

        clearTimeout(timeoutId)

        if (!response.ok) {
          throw new Error(`请求失败 [${response.status}]：${response.statusText}`)
        }

        const rawData = await response.json()
        console.log('后端返回原始数据:', rawData)

        // 核心：只保留3个合法区域的数据
        this.blastData = this.validZoneIds.reduce((obj, zoneId) => {
          // 优先使用后端数据，无数据则生成前端随机数据
          let zoneData = rawData[zoneId] || this.generateRandomBlastData(zoneId)
          // 校验数据并更新安全状态
          zoneData = this.validateData(zoneId, zoneData)
          obj[zoneId] = zoneData
          return obj
        }, {})

        // 初始化/更新图表
        this.$nextTick(() => {
          this.initCharts()
          this.updateCharts()
        })
      } catch (error) {
        console.error('数据获取失败:', error)
        // 友好的错误提示
        this.errorMsg = error.name === 'AbortError'
            ? '请求超时，已使用前端模拟数据！'
            : `数据加载失败，已使用前端模拟数据：${error.message}`

        // 后端接口异常时，生成前端随机数据
        this.blastData = this.validZoneIds.reduce((obj, zoneId) => {
          let zoneData = this.generateRandomBlastData(zoneId)
          zoneData = this.validateData(zoneId, zoneData)
          obj[zoneId] = zoneData
          return obj
        }, {})

        this.$nextTick(() => {
          this.initCharts()
          this.updateCharts()
        })
      } finally {
        this.isLoading = false
      }
    },

    /**
     * 初始化图表
     */
    initCharts() {
      // 初始化振动图表
      if (!this.vibrationChart) {
        const vibrationCtx = this.$refs.vibrationChart?.getContext('2d')
        if (vibrationCtx) {
          this.vibrationChart = new Chart(vibrationCtx, this.getChartConfig('振动水平 (mm/s)'))
        }
      }

      // 初始化噪音图表
      if (!this.noiseChart) {
        const noiseCtx = this.$refs.noiseChart?.getContext('2d')
        if (noiseCtx) {
          this.noiseChart = new Chart(noiseCtx, this.getChartConfig('噪音水平 (dB)'))
        }
      }
    },

    /**
     * 获取通用图表配置
     */
    getChartConfig(yTitle) {
      // 构建3个固定区域的数据集
      const datasets = this.validZoneIds.map(zoneId => ({
        label: this.zoneNameMap[zoneId],
        data: [],
        borderColor: this.chartColors[zoneId].border,
        backgroundColor: this.chartColors[zoneId].background,
        tension: 0.4,
        fill: true,
        borderWidth: 2
      }))

      return {
        type: 'line',
        data: {
          labels: [],
          datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: {duration: 300},
          scales: {
            y: {
              beginAtZero: true,
              grid: {color: 'rgba(0,0,0,0.05)'},
              title: {display: true, text: yTitle, font: {weight: 'bold'}}
            },
            x: {
              grid: {display: false},
              title: {display: true, text: '时间', font: {weight: 'bold'}}
            }
          },
          plugins: {
            legend: {position: 'top', align: 'end'},
            tooltip: {
              mode: 'index',
              intersect: false,
              padding: 10,
              boxPadding: 5
            }
          }
        }
      }
    },

    /**
     * 更新图表数据
     */
    updateCharts() {
      if (!this.hasValidData) return

      const now = new Date()
      const timeLabel = now.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })

      // 更新振动图表
      this.updateSingleChart(this.vibrationChart, 'vibrationLevel', timeLabel)
      // 更新噪音图表
      this.updateSingleChart(this.noiseChart, 'noiseLevel', timeLabel)
    },

    /**
     * 更新单个图表数据
     */
    updateSingleChart(chart, dataKey, timeLabel) {
      if (!chart) return

      // 保留最近10个数据点
      if (chart.data.labels.length >= 10) {
        chart.data.labels.shift()
        chart.data.datasets.forEach(ds => ds.data.shift())
      }

      // 添加新时间标签
      chart.data.labels.push(timeLabel)

      // 为3个固定区域更新数据
      this.validZoneIds.forEach((zoneId, index) => {
        const zoneData = this.blastData[zoneId] || {}
        chart.data.datasets[index].data.push(zoneData[dataKey] || 0)
      })

      chart.update()
    },

    /**
     * 🌟 分级获取安全状态样式类
     */
    getSafetyClass(status) {
      switch (status) {
        case '安全':
          return 'status-safe'
        case '警告':
          return 'status-warning'
        case '危险':
          return 'status-danger'
        default:
          return 'status-unknown'
      }
    },

    /**
     * 格式化安全状态显示文本
     */
    formatSafetyStatus(status) {
      return status || '未知'
    },

    /**
     * 格式化时间戳
     */
    formatTimestamp(timestamp) {
      if (!timestamp) return '未知时间'
      try {
        const dateStr = timestamp.replace('T', ' ')
        const date = new Date(dateStr)
        if (isNaN(date.getTime())) return '未知时间'
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
      } catch (e) {
        return '未知时间'
      }
    }
  }
}
</script>

<style scoped>
/* 全局样式重置 & 基础配置 */
.blast-monitoring {
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
  font-family: 'Microsoft Yahei', 'PingFang SC', sans-serif;
  color: #333;
}

/* 报警记录样式 */
.alarm-records {
  background: #fff1f0;
  border: 1px solid #ffccc7;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.alarm-title {
  margin: 0 0 12px 0;
  color: #f5222d;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.alarm-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 500px;
  overflow-y: auto;
}

.alarm-item {
  background: white;
  padding: 8px 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

/* 🌟 分级样式 */
.warning-item {
  border-left: 3px solid #faad14;
}

.danger-item {
  border-left: 3px solid #f5222d;
}

.alarm-time {
  color: #666;
  min-width: 120px;
}

.alarm-zone {
  color: #1f2937;
  font-weight: 600;
  min-width: 100px;
}

.alarm-indicator {
  min-width: 100px;
}

.alarm-level {
  min-width: 60px;
  font-weight: 600;
}

.warning-item .alarm-level {
  color: #faad14;
}

.danger-item .alarm-level {
  color: #f5222d;
}

.alarm-reason {
  flex: 1;
  color: #333;
}

.alarm-value {
  color: #999;
  min-width: 80px;
  text-align: right;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.refresh-btn:disabled {
  background-color: #8c8c8c;
  cursor: not-allowed;
}

.refresh-btn:hover:not(:disabled) {
  background-color: #096dd9;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.loading-text {
  font-size: 16px;
  color: #666;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 错误提示 */
.error-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 30px;
  background: #fff1f0;
  border: 1px solid #ffccc7;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.error-text {
  font-size: 14px;
  color: #f5222d;
  text-align: center;
  margin-bottom: 12px;
  line-height: 1.5;
}

.error-retry-btn {
  padding: 6px 16px;
  background-color: #f5222d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 13px;
}

.error-retry-btn:hover {
  background-color: #d91818;
}

/* 内容容器 */
.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 监控卡片网格 */
.monitoring-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 20px;
}

/* 区域卡片 */
.blast-zone-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.blast-zone-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 🌟 分级卡片样式 */
.card-warning {
  border-left: 4px solid #faad14;
  animation: pulse-warning 2s infinite;
}

.card-alarm {
  border-left: 4px solid #f5222d;
  animation: pulse-danger 2s infinite;
}

@keyframes pulse-warning {
  0% {
    box-shadow: 0 4px 16px rgba(250, 173, 20, 0.1);
  }
  50% {
    box-shadow: 0 4px 16px rgba(250, 173, 20, 0.2);
  }
  100% {
    box-shadow: 0 4px 16px rgba(250, 173, 20, 0.1);
  }
}

@keyframes pulse-danger {
  0% {
    box-shadow: 0 4px 16px rgba(245, 34, 45, 0.1);
  }
  50% {
    box-shadow: 0 4px 16px rgba(245, 34, 45, 0.2);
  }
  100% {
    box-shadow: 0 4px 16px rgba(245, 34, 45, 0.1);
  }
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.zone-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

/* 🌟 分级徽章 */
.warning-badge {
  padding: 4px 8px;
  background-color: #fff7e6;
  color: #faad14;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.alarm-badge {
  padding: 4px 8px;
  background-color: #fff1f0;
  color: #f5222d;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

/* 指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.metric-item {
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.metric-label {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.metric-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 🌟 分级指标文字 */
.warning-value {
  color: #faad14;
}

.danger-value {
  color: #f5222d;
}

.threshold-tip {
  font-size: 11px;
  color: #9ca3af;
  font-weight: normal;
}

/* 安全状态 */
.safety-status-wrapper {
  margin-bottom: 12px;
}

.safety-status {
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  font-weight: 600;
  font-size: 15px;
}

/* 🌟 分级状态样式 */
.status-safe {
  background-color: #f0f9ff;
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

.status-unknown {
  background-color: #f5f5f5;
  color: #8c8c8c;
}

.status-label {
  font-weight: normal;
  margin-right: 4px;
}

/* 更新时间 */
.update-time {
  font-size: 12px;
  color: #9ca3af;
  text-align: right;
}

/* 图表区域（保留原有样式） */
.charts-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.charts-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  height: 350px;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.chart-subtitle {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .monitoring-grid {
    grid-template-columns: 1fr;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-card {
    height: 300px;
  }

  .page-title {
    font-size: 20px;
  }

  .error-container {
    width: 90%;
    padding: 15px 20px;
  }

  .alarm-item {
    flex-wrap: wrap;
    gap: 4px;
  }

  .alarm-time, .alarm-zone, .alarm-indicator, .alarm-level {
    min-width: auto;
    margin-right: 8px;
  }
}

@media (max-width: 480px) {
  .blast-monitoring {
    padding: 12px;
  }

  .blast-zone-card {
    padding: 16px;
  }

  .charts-section {
    padding: 16px;
  }
}
</style>