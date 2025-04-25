package com.dcd.server.core.domain.workspace.dto.extension

import com.dcd.server.core.domain.application.dto.extenstion.toWorkspaceDto
import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceProfileResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceSimpleResDto
import com.dcd.server.core.domain.workspace.model.Workspace

fun Workspace.toDto(applicationList: List<Application>): WorkspaceResDto =
    WorkspaceResDto(
        id = this.id,
        title = this.title,
        description = this.description,
        owner = this.owner.toDto(),
        applicationList = applicationList.map { it.toWorkspaceDto() },
        globalEnv = this.globalEnv.associate { it.key to it.value }
    )

fun CreateWorkspaceReqDto.toEntity(user: User): Workspace =
    Workspace(
        title = this.title,
        description = this.description,
        owner = user
    )

fun Workspace.toProfileDto(applicationList: List<ApplicationProfileResDto>): WorkspaceProfileResDto =
    WorkspaceProfileResDto(
        id = this.id,
        title = this.title,
        applicationList = applicationList
    )

fun Workspace.toSimpleDto(applicationList: List<Application>): WorkspaceSimpleResDto =
    WorkspaceSimpleResDto(
        id = this.id,
        title = this.title,
        description = this.description,
        applicationList = applicationList.map { it.toWorkspaceDto() }
    )