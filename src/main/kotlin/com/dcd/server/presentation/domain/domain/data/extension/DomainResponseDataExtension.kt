package com.dcd.server.presentation.domain.domain.data.extension

import com.dcd.server.core.domain.domain.dto.response.CreateDomainResDto
import com.dcd.server.core.domain.domain.dto.response.DomainResDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.domain.data.response.CreateDomainResponse
import com.dcd.server.presentation.domain.domain.data.response.DomainResponse

fun CreateDomainResDto.toResponse(): CreateDomainResponse =
    CreateDomainResponse(
        domainId = domainId
    )

fun DomainResDto.toResponse(): DomainResponse =
    DomainResponse(
        id = id,
        name = name,
        description = description,
        application = application?.toResponse(),
    )