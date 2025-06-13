package com.dcd.server.infrastructure.global.config

import com.dcd.server.core.common.socket.SocketHandler
import com.dcd.server.infrastructure.global.socket.interceptor.WebSocketInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
class WebSocketConfig(
    private val socketHandler: SocketHandler,
    private val webSocketInterceptor: WebSocketInterceptor
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(socketHandler, "/application/exec")
            .addInterceptors(webSocketInterceptor)
            .setAllowedOrigins("*")
    }
}

