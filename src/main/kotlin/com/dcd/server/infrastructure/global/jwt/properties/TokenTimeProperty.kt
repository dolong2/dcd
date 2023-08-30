package com.dcd.server.infrastructure.global.jwt.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt.time")
class TokenTimeProperty (
    val accessTime: Long,
    val refreshTime: Long
)