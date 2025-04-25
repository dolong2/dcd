package com.dcd.server.core.domain.env.model

import java.util.*

data class GlobalEnv(
    val id: UUID = UUID.randomUUID(),
    val key: String,
    var value: String,
    val encryption: Boolean
)