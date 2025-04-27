package com.dcd.server.core.domain.workspace.dto.request

import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto

data class PutGlobalEnvReqDto(
    val envList: List<PutEnvReqDto>
)
