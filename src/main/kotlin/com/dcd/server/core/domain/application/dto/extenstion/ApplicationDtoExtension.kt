package com.dcd.server.core.domain.application.dto.extenstion

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceApplicationResDto
import com.dcd.server.core.domain.workspace.model.Workspace

fun CreateApplicationReqDto.toEntity(workspace: Workspace, externalPort: Int): Application =
    Application(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        workspace = workspace,
        port = this.port,
        externalPort = externalPort,
        version = this.version,
        status = ApplicationStatus.CREATED,
        labels = this.labels
    )

fun Application.toDto(): ApplicationResDto =
    ApplicationResDto(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        port = this.port,
        externalPort = this.externalPort,
        version = this.version,
        status = this.status
    )

fun Application.toProfileDto(): ApplicationProfileResDto =
    ApplicationProfileResDto(
        id = this.id,
        name = this.name,
        description = this.description
    )

fun Application.toWorkspaceDto(): WorkspaceApplicationResDto =
    WorkspaceApplicationResDto(
        id = this.id,
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        port = this.port,
        externalPort = this.externalPort,
        status = this.status
    )