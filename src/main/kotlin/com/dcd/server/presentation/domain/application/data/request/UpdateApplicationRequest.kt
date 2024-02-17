package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class UpdateApplicationRequest(
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String?,
    val version: String,
    val port: Int
)
