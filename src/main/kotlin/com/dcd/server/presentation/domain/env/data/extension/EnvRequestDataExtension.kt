package com.dcd.server.presentation.domain.env.data.extension

import com.dcd.server.core.domain.env.dto.request.AddEnvReqDto
import com.dcd.server.presentation.domain.env.data.request.AddEnvRequest

fun AddEnvRequest.toDto(): AddEnvReqDto =
    AddEnvReqDto(
        key = this.key,
        value = this.value
    )