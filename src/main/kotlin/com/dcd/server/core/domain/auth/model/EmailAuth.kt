package com.dcd.server.core.domain.auth.model

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import java.util.*

data class EmailAuth(
    val email: String,
    val code: String = UUID.randomUUID().toString().split("-")[0],
    val certificate: Boolean = false,
    val usage: EmailAuthUsage
)