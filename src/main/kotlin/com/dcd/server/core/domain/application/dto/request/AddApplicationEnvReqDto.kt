package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.env.dto.request.AddEnvReqDto

data class AddApplicationEnvReqDto(
    val envList: List<AddEnvReqDto>
)