package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.response.ApplicationListResponseDto
import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResponseDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResponseDto
import com.dcd.server.presentation.domain.application.data.response.ApplicationListResponse
import com.dcd.server.presentation.domain.application.data.response.ApplicationProfileResponse
import com.dcd.server.presentation.domain.application.data.response.ApplicationResponse

fun ApplicationResponseDto.toResponse(): ApplicationResponse =
    ApplicationResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType,
        env = this.env
    )

fun ApplicationListResponseDto.toResponse(): ApplicationListResponse =
    ApplicationListResponse(
        list = this.list.map { it.toResponse() }
    )

fun ApplicationProfileResponseDto.toResponse(): ApplicationProfileResponse =
    ApplicationProfileResponse(
        id = this.id,
        name = this.name,
        description = this.description
    )