package com.dcd.server.presentation.domain.workspace.data.exetension

import com.dcd.server.core.domain.workspace.dto.response.WorkspaceListResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.user.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceListResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceResponse

fun WorkspaceResDto.toResponse(): WorkspaceResponse =
    WorkspaceResponse(
        id =  this.id,
        title = this.title,
        description = this.description,
        applicationList = this.applicationList.map { it.toResponse() },
        owner = this.owner.toResponse()
    )

fun WorkspaceListResDto.toResponse(): WorkspaceListResponse =
    WorkspaceListResponse(
        list = this.list.map { it.toResponse() }
    )