package com.dcd.server.core.domain.auth.model

data class RefreshToken(
    val userId: String,
    val token: String,
    val refreshTTL: Long
)
