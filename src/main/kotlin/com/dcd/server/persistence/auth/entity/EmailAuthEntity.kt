package com.dcd.server.persistence.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash(value = "EmailAuth")
class EmailAuthEntity(
    @Indexed
    val email: String,
    @Id
    @Indexed
    val code: String
)