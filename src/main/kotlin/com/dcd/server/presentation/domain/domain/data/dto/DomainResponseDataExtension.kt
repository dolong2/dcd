package com.dcd.server.presentation.domain.domain.data.dto

import com.dcd.server.core.domain.domain.dto.response.CreateDomainResDto
import com.dcd.server.presentation.domain.domain.data.response.CreateDomainResponse

fun CreateDomainResDto.toResponse(): CreateDomainResponse =
    CreateDomainResponse(
        domainId = domainId
    )