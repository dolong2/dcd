package com.dcd.server.presentation.domain.workspace.data.exetension

import com.dcd.server.core.domain.workspace.dto.response.WorkspaceListResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceProfileResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceSimpleResDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.user.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceListResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceProfileResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceSimpleResponse

fun WorkspaceResDto.toResponse(): WorkspaceResponse =
    WorkspaceResponse(
        id =  this.id,
        title = this.title,
        description = this.description,
        applicationList = this.applicationList.map { it.toResponse() },
        globalEnv = this.globalEnv,
        owner = this.owner.toResponse()
    )

fun WorkspaceSimpleResDto.toResponse(): WorkspaceSimpleResponse =
    WorkspaceSimpleResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        applicationList = this.applicationList.map { it.toResponse() }
    )

fun WorkspaceListResDto.toResponse(): WorkspaceListResponse =
    WorkspaceListResponse(
        list = this.list.map { it.toResponse() }
    )

fun WorkspaceProfileResDto.toResponse(): WorkspaceProfileResponse =
    WorkspaceProfileResponse(
        id = this.id,
        title = this.title,
        applicationList = this.applicationList.map { it.toResponse() }
    )