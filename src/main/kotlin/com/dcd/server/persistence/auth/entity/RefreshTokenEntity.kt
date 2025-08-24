package com.dcd.server.persistence.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash(value = "RefreshToken")
class RefreshTokenEntity(
    @Id
    @Indexed
    val token: String,
    @Indexed
    val userId: String,
    @TimeToLive
    var refreshTTL: Long
)