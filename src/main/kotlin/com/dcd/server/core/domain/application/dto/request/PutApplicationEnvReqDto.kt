package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto

data class PutApplicationEnvReqDto(
    val name: String,
    val description: String,
    val envList: List<PutEnvReqDto>
)