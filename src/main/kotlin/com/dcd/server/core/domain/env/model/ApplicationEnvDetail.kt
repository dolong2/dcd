package com.dcd.server.core.domain.env.model

import java.util.UUID

class ApplicationEnvDetail(
    val id: UUID,
    val key: String,
    val value: String,
    val encryption: Boolean = false,
    val applicationEnv: ApplicationEnv,
)