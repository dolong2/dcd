package com.dcd.server.core.domain.domain.dto.response

import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResDto

data class DomainResDto(
    val id: String,
    val name: String,
    val description: String,
    val application: ApplicationProfileResDto?,
)
