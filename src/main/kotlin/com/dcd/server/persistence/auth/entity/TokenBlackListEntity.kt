package com.dcd.server.persistence.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash(value = "TokenBlackList")
class TokenBlackListEntity(
    @Id
    val token: String,
    @TimeToLive
    var ttl: Long,
)