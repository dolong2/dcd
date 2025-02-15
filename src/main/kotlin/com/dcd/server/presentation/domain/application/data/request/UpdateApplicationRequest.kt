package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateApplicationRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9 ]{1,26}$")
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String?,
    val version: String,
    val port: Int
)
