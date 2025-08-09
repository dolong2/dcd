package com.dcd.server.core.domain.env.dto.response

import java.util.UUID

data class ApplicationEnvResDto(
    val id: UUID,
    val name: String,
    val description: String,
    val details: List<ApplicationEnvDetailResDto>
)
