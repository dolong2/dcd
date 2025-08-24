package com.dcd.server.presentation.domain.workspace.data.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateWorkspaceRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^(?!host$|bridge$|none$)[a-zA-Z0-9 ]+$")
    val title: String,
    val description: String
)
