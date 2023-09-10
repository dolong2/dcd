package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class CreateApplicationReqDto(
    val name: String,
    val description: String?,
    val githubUrl: String,
    val applicationType: ApplicationType
)