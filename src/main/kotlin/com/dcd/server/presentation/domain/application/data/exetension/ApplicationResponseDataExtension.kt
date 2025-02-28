package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.extenstion.toWorkspaceDto
import com.dcd.server.core.domain.application.dto.response.*
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceApplicationResDto
import com.dcd.server.presentation.domain.application.data.response.*
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceApplicationResponse

fun ApplicationResDto.toResponse(): ApplicationResponse =
    ApplicationResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        port = this.port,
        externalPort = this.externalPort,
        version = this.version,
        status = this.status,
        failureReason = this.failureReason,
        labels = this.labels
    )

fun ApplicationListResDto.toResponse(): ApplicationListResponse =
    ApplicationListResponse(
        list = this.list.map { it.toResponse() }
    )

fun ApplicationProfileResDto.toResponse(): ApplicationProfileResponse =
    ApplicationProfileResponse(
        id = this.id,
        name = this.name,
        description = this.description
    )

fun AvailableVersionResDto.toResponse(): AvailableVersionResponse =
    AvailableVersionResponse(
        version = this.version
    )

fun ApplicationLogResDto.toResponse(): ApplicationLogResponse =
    ApplicationLogResponse(
        logs = this.logs
    )

fun WorkspaceApplicationResDto.toResponse(): WorkspaceApplicationResponse =
    WorkspaceApplicationResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        port = this.port,
        externalPort = this.externalPort,
        status = this.status
    )

fun CommandResultResDto.toResponse(): CommandResultResponse =
    CommandResultResponse(
        result = this.result
    )

fun CreateApplicationResDto.toResponse(): CreateApplicationResponse =
    CreateApplicationResponse(
        applicationId = this.applicationId
    )

fun ApplicationTypeListResDto.toResponse(): ApplicationTypeListResponse =
    ApplicationTypeListResponse(
        list = this.list
    )