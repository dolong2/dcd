package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class UpdateApplicationReqDto(
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String?,
    val version: String,
    val port: Int
)
