package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.request.SpringApplicationRunReqDto
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.SpringApplicationRunRequest

fun CreateApplicationRequest.toDto(): CreateApplicationReqDto =
    CreateApplicationReqDto(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        env = this.env,
        applicationType = this.applicationType
    )

fun SpringApplicationRunRequest.toDto(): SpringApplicationRunReqDto =
    SpringApplicationRunReqDto(
        langVersion = this.langVersion,
        dbTypes = this.dbTypes,
        rootPassword = this.rootPassword,
        dataBaseName = this.dataBaseName
    )