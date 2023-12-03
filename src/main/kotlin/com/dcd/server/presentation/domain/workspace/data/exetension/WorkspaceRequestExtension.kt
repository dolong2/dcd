package com.dcd.server.presentation.domain.workspace.data.exetension

import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest

fun CreateWorkspaceRequest.toDto(): CreateWorkspaceReqDto =
    CreateWorkspaceReqDto(
        title = this.title,
        description = this.description
    )