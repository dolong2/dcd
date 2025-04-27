package com.dcd.server.presentation.domain.workspace.data.request

import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest

data class PutGlobalEnvRequest(
    val envList: List<PutEnvRequest>
)
