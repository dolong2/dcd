package com.dcd.server.core.domain.auth.model

class TokenBlackList(
    val token: String,
    val ttl: Long,
)