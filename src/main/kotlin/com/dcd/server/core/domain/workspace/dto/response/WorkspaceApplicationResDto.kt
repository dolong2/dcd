package com.dcd.server.core.domain.workspace.dto.response

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class WorkspaceApplicationResDto(
    val id: String,
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val port: Int,
    val externalPort: Int,
    val status: ApplicationStatus
)