package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.response.*
import com.dcd.server.presentation.domain.application.data.response.*

fun ApplicationResDto.toResponse(): ApplicationResponse =
    ApplicationResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env,
        port = this.port,
        version = this.version,
        status = this.status
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

fun RunApplicationResDto.toResponse(): RunApplicationResponse =
    RunApplicationResponse(
        externalPort = this.externalPort
    )

fun ApplicationLogResDto.toResponse(): ApplicationLogResponse =
    ApplicationLogResponse(
        logs = this.logs
    )