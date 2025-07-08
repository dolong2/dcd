package com.dcd.server.presentation.domain.domain.data.dto

import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest

fun CreateDomainRequest.toDto(): CreateDomainReqDto =
    CreateDomainReqDto(
        name = name,
        description = description
    )