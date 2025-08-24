package com.dcd.server.persistence.auth.entity

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash(value = "EmailAuth", timeToLive = 60 * 3)
class EmailAuthEntity(
    @Indexed
    val email: String,
    @Id
    @Indexed
    val code: String,
    val certificate: Boolean,
    val usage: EmailAuthUsage
)