<template>
  <div class="home-dashboard">
    <Dashboard :sensorData="sensorData" />

    <div class="real-time-monitoring">
      <h3>实时数据流
        <!-- 连接状态提示 -->
        <span class="ws-status" :class="{ connected: wsConnected, disconnected: !wsConnected }">
          {{ wsConnected ? '✅ 已连接' : '❌ 未连接' }}
        </span>
      </h3>
      <div class="data-stream">
        <!-- 空数据/连接失败提示 -->
        <div v-if="!wsConnected" class="empty-tip">
          WebSocket连接失败，请检查后端服务或代理配置！
        </div>
        <div v-else-if="dataStream.length === 0" class="empty-tip">
          已连接，但暂无实时数据推送...
        </div>
        <div v-else v-for="(item, index) in dataStream" :key="index" class="data-item">
          <span class="data-time">{{ item.time }}</span>
          <span class="data-sensor">{{ item.sensor }}</span>
          <span class="data-value">{{ item.value }}</span>
          <span class="data-unit">{{ item.unit }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Dashboard from './Dashboard.vue'

export default {
  name: 'HomeDashboard',
  components: {
    Dashboard
  },
  data() {
    return {
      ws: null,
      reconnectTimer: null,
      isReconnecting: false,
      sensorData: {}, // 传感器卡片数据
      dataStream: [], // 实时数据流
      wsConnected: false, // WS连接状态
      reconnectInterval: 3000, // 重连间隔（3秒）
      maxStreamLength: 20 // 数据流最大条数
    }
  },
  // 生命周期：挂载时初始化WS
  mounted() {
    console.log('HomeDashboard 挂载，初始化WebSocket...')
    this.initWebSocket()
  },
  // 生命周期：卸载时清理WS
  beforeUnmount() {
    console.log('HomeDashboard 卸载，清理WebSocket资源')
    this.cleanupWebSocket()
  },
  methods: {
    // 初始化WebSocket
    initWebSocket() {
      // 防止重复重连
      if (this.isReconnecting) return
      this.isReconnecting = true
      this.wsConnected = false

      // 清理旧连接
      this.cleanupWebSocket()

      try {
        // 1. 适配HTTP/HTTPS的WS协议
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
        // 2. WS地址（优先用环境变量，方便部署）
        const wsUrl = import.meta.env.VITE_WS_URL || `${protocol}//${window.location.host}/ws`
        console.log('尝试连接WebSocket:', wsUrl)

        this.ws = new WebSocket(wsUrl)

        // 连接成功回调
        this.ws.onopen = () => {
          console.log('✅ WebSocket连接成功！')
          this.wsConnected = true
          this.isReconnecting = false
          // 可选：向后端发送订阅指令
          this.sendWsMessage({ type: 'subscribe', topic: 'sensor_data' })
        }

        // 接收数据回调
        this.ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            console.log('📥 收到WS数据:', data)

            // 过滤掉pump_primary传感器数据（核心修改1）
            const filteredSensorData = {}
            Object.entries(data).forEach(([sensorId, sensorInfo]) => {
              if (sensorId !== 'pump_primary') {
                filteredSensorData[sensorId] = sensorInfo
              }
            })
            // 更新传感器卡片数据（仅保留非pump_primary的数据）
            this.sensorData = { ...filteredSensorData }

            // 更新实时数据流（遍历所有传感器，过滤pump_primary）
            Object.entries(data).forEach(([sensorId, sensorInfo]) => {
              // 跳过pump_primary传感器（核心修改1）
              if (sensorId === 'pump_primary') return

              // 构造数据流项（适配你的sensorData结构）
              const streamItem = {
                time: this.formatTime(sensorInfo?.data?.timestamp),
                sensor: sensorId,
                value: (sensorInfo?.data?.temperature ?? 0).toFixed(1),
                unit: '°C' // 可根据需要切换为其他指标
              }
              this.dataStream.push(streamItem)
              // 限制数据流长度
              if (this.dataStream.length > this.maxStreamLength) {
                this.dataStream.shift()
              }
            })
          } catch (e) {
            console.error('❌ WS数据解析失败:', e, '原始数据:', event.data)
          }
        }

        // 连接关闭回调
        this.ws.onclose = (event) => {
          console.log(`❌ WebSocket断开（状态码：${event.code}），原因：${event.reason}`)
          this.wsConnected = false
          this.isReconnecting = false
          // 非正常关闭才重连（1000=正常关闭）
          if (event.code !== 1000) {
            console.log(`⏳ ${this.reconnectInterval/1000}秒后尝试重连...`)
            this.reconnectTimer = setTimeout(() => this.initWebSocket(), this.reconnectInterval)
          }
        }

        // 连接错误回调
        this.ws.onerror = (error) => {
          console.error('❌ WebSocket错误:', error)
          this.wsConnected = false
          this.ws.close() // 触发onclose重连
        }
      } catch (e) {
        console.error('❌ 创建WebSocket失败:', e)
        this.isReconnecting = false
        // 失败后重连
        this.reconnectTimer = setTimeout(() => this.initWebSocket(), this.reconnectInterval)
      }
    },

    // 清理WebSocket资源
    cleanupWebSocket() {
      // 清除重连定时器
      if (this.reconnectTimer) {
        clearTimeout(this.reconnectTimer)
        this.reconnectTimer = null
      }
      // 关闭WS连接
      if (this.ws) {
        try {
          this.ws.close(1000, '主动关闭连接')
        } catch (e) {
          console.warn('关闭WS连接失败:', e)
        }
        this.ws = null
      }
    },

    // 发送WS消息（封装，方便调用）
    sendWsMessage(message) {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify(message))
      } else {
        console.warn('WS未连接，无法发送消息:', message)
      }
    },

    // 格式化时间（兼容空值）
    formatTime(timestamp) {
      if (!timestamp) return new Date().toLocaleTimeString()
      return new Date(timestamp).toLocaleTimeString()
    }
  }
}
</script>

<style scoped>
.home-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
}

.real-time-monitoring {
  margin-top: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.real-time-monitoring h3 {
  color: darkgrey;
  margin-bottom: 1rem;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* WS状态样式 */
.ws-status {
  font-size: 0.8rem;
  font-weight: normal;
}
.ws-status.connected {
  color: #52c41a;
}
.ws-status.disconnected {
  color: #f5222d;
}

.data-stream {
  max-height: 500px;
  overflow-y: auto;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
}

/* 空提示样式 */
.empty-tip {
  text-align: center;
  padding: 2rem;
  color: #999;
  font-size: 0.9rem;
}

.data-item {
  display: grid;
  grid-template-columns: 100px 150px 100px 80px;
  padding: 0.8rem 1rem;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.data-item:hover {
  background-color: #f5f7fa;
}

.data-time {
  color: #666;
  font-size: 0.8rem;
}

.data-sensor {
  color: #333;
  font-weight: bold;
}

.data-value {
  color: #1890ff;
  font-weight: bold;
}

.data-unit {
  color: #999;
  font-size: 0.8rem;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .data-item {
    grid-template-columns: 80px 120px 80px 60px;
    font-size: 0.9rem;
  }
  /* 移动端同步延长数据流高度 */
  .data-stream {
    max-height: 400px;
  }
}
</style>