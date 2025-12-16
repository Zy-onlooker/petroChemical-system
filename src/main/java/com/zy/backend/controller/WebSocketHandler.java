package com.zy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zy.backend.model.MonitoringData;
import com.zy.backend.model.SensorData;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketHandler extends TextWebSocketHandler implements DisposableBean {
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public WebSocketHandler() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 降低推送频率，避免连接被压垮
        scheduler.scheduleAtFixedRate(this::sendDataToAll, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    // 处理WebSocket传输错误，移除异常会话
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessions.remove(session);
        exception.printStackTrace();
    }

    private void sendDataToAll() {
        String[] sensors = {"reactor_1", "pipeline_a", "tank_main", "pump_primary"};
        Map<String, MonitoringData> dataMap = new HashMap<>();

        for (String sensorId : sensors) {
            MonitoringData data = new MonitoringData();
            data.setSensorId(sensorId);
            data.setData(generateRandomData());
            data.setStatus("normal");
            dataMap.put(sensorId, data);
        }

        try {
            String jsonData = objectMapper.writeValueAsString(dataMap);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonData));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SensorData generateRandomData() {
        SensorData data = new SensorData();
        data.setTimestamp(LocalDateTime.now());
        data.setTemperature(20 + random.nextDouble() * 30);
        data.setPressure(1 + random.nextDouble() * 4);
        data.setFlowRate(10 + random.nextDouble() * 90);
        data.setPh(6 + random.nextDouble() * 2);
        data.setTurbidity(random.nextDouble() * 10);
        data.setConductivity(100 + random.nextDouble() * 900);
        return data;
    }

    // 销毁时关闭线程池，避免内存泄漏
    @Override
    public void destroy() throws Exception {
        scheduler.shutdown();
        if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
            scheduler.shutdownNow();
        }
    }
}