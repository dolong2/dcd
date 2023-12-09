package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import jakarta.validation.constraints.NotBlank

data class CreateApplicationRequest(
    @field:NotBlank
    val name: String,
    val description: String?,
    @field:NotBlank
    val githubUrl: String,
    val env: Map<String, String>,
    val applicationType: ApplicationType,
    val port: Int
)