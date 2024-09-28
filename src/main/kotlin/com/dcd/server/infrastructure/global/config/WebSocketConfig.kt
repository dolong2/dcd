package com.dcd.server.infrastructure.global.config

import com.dcd.server.presentation.domain.application.data.ApplicationSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
class WebSocketConfig(
    private val dockerWebSocketHandler: ApplicationSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(dockerWebSocketHandler, "/application/exec")
            .setAllowedOrigins("*")
    }
}

