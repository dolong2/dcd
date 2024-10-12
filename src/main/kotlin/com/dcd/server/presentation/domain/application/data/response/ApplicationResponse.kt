package com.dcd.server.presentation.domain.application.data.response

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class ApplicationResponse(
    val id: String,
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String?,
    val env: Map<String, String>,
    val port: Int,
    val externalPort: Int,
    val version: String,
    val status: ApplicationStatus,
    val labels: List<String>
)