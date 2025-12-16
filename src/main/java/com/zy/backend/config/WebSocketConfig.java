package com.zy.backend.config;

import com.zy.backend.controller.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket // 必须加这个注解启用WebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // 注册WebSocket处理器
    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }

    // 注册握手拦截器（解决跨域+传递会话）
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HttpSessionHandshakeInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/ws") // 映射/ws路径
                .addInterceptors(handshakeInterceptor())
                .setAllowedOriginPatterns("*"); // 兼容跨域（替代allowedOrigins）
    }
}