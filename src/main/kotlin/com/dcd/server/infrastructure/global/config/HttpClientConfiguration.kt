package com.dcd.server.infrastructure.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient
import java.time.Duration

@Configuration
class HttpClientConfiguration {
    @Bean
    fun httpClient(): HttpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build()
}