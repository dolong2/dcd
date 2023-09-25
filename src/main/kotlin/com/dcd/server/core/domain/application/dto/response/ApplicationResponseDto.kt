package com.dcd.server.core.domain.application.dto.response

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import java.util.*

data class ApplicationResponseDto(
    val id: String,
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String,
    val env: Map<String, String>
)