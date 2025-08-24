package com.dcd.server.presentation.domain.env.data.request

data class PutEnvRequest(
    val key: String,
    val value: String,
    val encryption: Boolean
)
