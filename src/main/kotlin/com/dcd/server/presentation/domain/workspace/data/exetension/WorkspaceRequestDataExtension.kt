package com.dcd.server.presentation.domain.workspace.data.exetension

import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.request.UpdateWorkspaceRequest

fun CreateWorkspaceRequest.toDto(): CreateWorkspaceReqDto =
    CreateWorkspaceReqDto(
        title = this.title,
        description = this.description
    )

fun UpdateWorkspaceRequest.toDto(): UpdateWorkspaceReqDto =
    UpdateWorkspaceReqDto(
        title = this.title,
        description = this.description
    )