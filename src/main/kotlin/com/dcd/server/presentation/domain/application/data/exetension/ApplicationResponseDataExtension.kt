package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.dto.response.AvailableVersionResDto
import com.dcd.server.presentation.domain.application.data.response.ApplicationListResponse
import com.dcd.server.presentation.domain.application.data.response.ApplicationProfileResponse
import com.dcd.server.presentation.domain.application.data.response.ApplicationResponse
import com.dcd.server.presentation.domain.application.data.response.AvailableVersionResponse

fun ApplicationResDto.toResponse(): ApplicationResponse =
    ApplicationResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env
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