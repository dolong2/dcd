package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest

fun CreateApplicationRequest.toDto(): CreateApplicationReqDto =
    CreateApplicationReqDto(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType
    )