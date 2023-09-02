package com.dcd.server.core.domain.auth.model

import java.util.*

data class EmailAuth(
    val email: String,
    val code: String = UUID.randomUUID().toString().split("-")[0],
)