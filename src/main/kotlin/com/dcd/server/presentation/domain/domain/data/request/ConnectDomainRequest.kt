package com.dcd.server.presentation.domain.domain.data.request

import jakarta.validation.constraints.NotBlank

data class ConnectDomainRequest(
    @field:NotBlank
    val applicationId: String
)
