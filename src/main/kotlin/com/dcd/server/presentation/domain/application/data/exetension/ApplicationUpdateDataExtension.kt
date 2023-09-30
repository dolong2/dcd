package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.presentation.domain.application.data.request.AddApplicationEnvRequest

fun AddApplicationEnvRequest.toDto(): AddApplicationEnvReqDto =
    AddApplicationEnvReqDto(
        envList = this.envList
    )