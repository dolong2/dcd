package com.dcd.server.presentation.domain.env.data.response

import java.util.UUID

data class ApplicationEnvSimpleResponse(
    val id: UUID,
    val name: String,
    val description: String
)
