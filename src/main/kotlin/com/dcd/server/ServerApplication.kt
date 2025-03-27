package com.dcd.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.socket.config.annotation.EnableWebSocket

@EnableCaching
@EnableWebSocket
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
class ServerApplication

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
