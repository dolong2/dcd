package com.dcd.server.presentation.domain.domain.data.extension

import com.dcd.server.core.domain.domain.dto.request.ConnectDomainReqDto
import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.presentation.domain.domain.data.request.ConnectDomainRequest
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest

fun CreateDomainRequest.toDto(): CreateDomainReqDto =
    CreateDomainReqDto(
        name = name,
        description = description
    )

fun ConnectDomainRequest.toDto(): ConnectDomainReqDto =
    ConnectDomainReqDto(
        applicationId = this.applicationId,
    )