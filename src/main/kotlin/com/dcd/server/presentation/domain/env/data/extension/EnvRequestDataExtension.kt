package com.dcd.server.presentation.domain.env.data.extension

import com.dcd.server.core.domain.env.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto
import com.dcd.server.presentation.domain.env.data.request.PutApplicationEnvRequest
import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest

fun PutEnvRequest.toDto(): PutEnvReqDto =
    PutEnvReqDto(
        key = this.key,
        value = this.value,
        encryption = this.encryption
    )

fun PutApplicationEnvRequest.toDto(): PutApplicationEnvReqDto =
    PutApplicationEnvReqDto(
        name = this.name,
        description = this.description,
        details = this.details.map { it.toDto() },
        applicationIdList = this.applicationIdList,
        applicationLabelList = this.applicationLabelList
    )