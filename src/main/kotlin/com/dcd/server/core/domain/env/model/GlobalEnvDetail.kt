package com.dcd.server.core.domain.env.model
import java.util.*

class GlobalEnvDetail(
    val id: UUID = UUID.randomUUID(),
    val key: String,
    val value: String,
    val encryption: Boolean = false
)