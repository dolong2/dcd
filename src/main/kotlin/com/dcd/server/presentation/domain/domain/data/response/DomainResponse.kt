package com.dcd.server.presentation.domain.domain.data.response

import com.dcd.server.presentation.domain.application.data.response.ApplicationProfileResponse

data class DomainResponse(
    val id: String,
    val name: String,
    val description: String,
    val application: ApplicationProfileResponse?,
)
