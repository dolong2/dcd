package com.dcd.server.core.domain.workspace.dto.request

import com.dcd.server.core.domain.env.dto.request.AddEnvReqDto

data class AddGlobalEnvReqDto(
    val envList: List<AddEnvReqDto>
)
