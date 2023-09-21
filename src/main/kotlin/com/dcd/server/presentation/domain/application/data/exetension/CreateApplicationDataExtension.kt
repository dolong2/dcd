package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.request.RunApplicationReqDto
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.RunApplicationRequest

fun CreateApplicationRequest.toDto(): CreateApplicationReqDto =
    CreateApplicationReqDto(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        applicationType = this.applicationType
    )

fun RunApplicationRequest.toDto(): RunApplicationReqDto =
    RunApplicationReqDto(
        langVersion = this.langVersion,
        dbTypes = this.dbTypes,
        rootPassword = this.rootPassword,
        dataBaseName = this.dataBaseName
    )