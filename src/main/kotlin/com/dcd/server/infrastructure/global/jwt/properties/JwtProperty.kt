package com.dcd.server.infrastructure.global.jwt.properties

import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.Key

@ConfigurationProperties("jwt")
class JwtProperty (
    accessSecret: String,
    refreshSecret: String
) {
    val accessSecret: Key
    val refreshSecret: Key

    init {
        this.accessSecret = Keys.hmacShaKeyFor(accessSecret.toByteArray())
        this.refreshSecret = Keys.hmacShaKeyFor(refreshSecret.toByteArray())
    }
}