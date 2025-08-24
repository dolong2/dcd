package com.dcd.server.core.domain.env.dto.response

data class ApplicationEnvDetailResDto(
    val key: String,
    val value: String,
    val encryption: Boolean
)
