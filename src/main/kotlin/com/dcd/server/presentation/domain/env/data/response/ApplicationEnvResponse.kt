package com.dcd.server.presentation.domain.env.data.response

import java.util.UUID

data class ApplicationEnvResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val details: List<ApplicationEnvDetailResponse>
)
