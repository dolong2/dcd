package com.dcd.server.core.domain.application.dto.extenstion

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResponseDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace

fun CreateApplicationReqDto.toEntity(workspace: Workspace): Application =
    Application(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        workspace = workspace,
        port = this.port
    )

fun Application.toDto(): ApplicationResponseDto =
    ApplicationResponseDto(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        port = this.port
    )