package com.dcd.server.presentation.domain.env.data.extension

import com.dcd.server.core.domain.env.dto.response.ApplicationEnvListResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvSimpleResDto
import com.dcd.server.presentation.domain.env.data.response.ApplicationEnvListResponse
import com.dcd.server.presentation.domain.env.data.response.ApplicationEnvSimpleResponse

fun ApplicationEnvSimpleResDto.toResponse(): ApplicationEnvSimpleResponse =
    ApplicationEnvSimpleResponse(
        id = this.id,
        name = this.name,
        description = this.description,
    )

fun ApplicationEnvListResDto.toResponse(): ApplicationEnvListResponse =
    ApplicationEnvListResponse(
        list = this.list.map { it.toResponse() }
    )