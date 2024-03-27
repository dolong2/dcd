package com.dcd.server.presentation.domain.workspace.data.response

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class WorkspaceApplicationResponse(
    val id: String,
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val port: Int,
    val externalPort: Int,
    val status: ApplicationStatus
)