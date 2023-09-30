package com.dcd.server.core.domain.application.dto.request

data class AddApplicationEnvReqDto(
    val envList: Map<String, String>
)