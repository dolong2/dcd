package com.dcd.server.core.domain.env.model

import java.util.*

data class ApplicationEnv(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val details: List<ApplicationEnvDetail>
)