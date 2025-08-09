package com.dcd.server.presentation.domain.env.data.response

data class ApplicationEnvDetailResponse(
    val key: String,
    val value: String,
    val encryption: Boolean
)
