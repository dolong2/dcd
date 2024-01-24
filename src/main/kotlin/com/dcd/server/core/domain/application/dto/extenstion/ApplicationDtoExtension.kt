package com.dcd.server.core.domain.application.dto.extenstion

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.workspace.model.Workspace

fun CreateApplicationReqDto.toEntity(workspace: Workspace): Application =
    Application(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        workspace = workspace,
        port = this.port,
        version = this.version
    )

fun Application.toDto(): ApplicationResDto =
    ApplicationResDto(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        port = this.port
    )

fun Application.toProfileDto(): ApplicationProfileResDto =
    ApplicationProfileResDto(
        id = this.id,
        name = this.name,
        description = this.description
    )