package com.dcd.server.presentation.domain.application.data.request

data class AddApplicationEnvRequest(
    val envList: Map<String, String>
)