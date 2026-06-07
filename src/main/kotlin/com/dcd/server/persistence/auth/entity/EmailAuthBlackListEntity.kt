package com.dcd.server.persistence.auth.entity

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.time.LocalDateTime

@RedisHash(value = "EmailAuthBlackList", timeToLive = 60 * 30)
class EmailAuthBlackListEntity(
    @Id
    val id: String,
    @Indexed
    val email: String,
    @Indexed
    val usage: EmailAuthUsage,
    val blockedAt: LocalDateTime = LocalDateTime.now()
)
