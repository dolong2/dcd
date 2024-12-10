package com.dcd.server.infrastructure.global.config

import com.dcd.server.infrastructure.global.filter.WebSocketFilter
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.infrastructure.global.socket.ApplicationSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
class WebSocketConfig(
    private val dockerWebSocketHandler: ApplicationSocketHandler,
    private val parseTokenAdapter: ParseTokenAdapter
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(dockerWebSocketHandler, "/application/exec")
            .addInterceptors(WebSocketFilter(parseTokenAdapter))
            .setAllowedOrigins("*")
    }
}

