package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.application.model.enums.ApplicationType

data class UpdateApplicationReqDto(
    val name: String,
    val description: String?,
    val githubUrl: String?,
    val applicationType: ApplicationType,
    val port: Int,
    val version: String,
    val initialScripts: List<String>,
)
