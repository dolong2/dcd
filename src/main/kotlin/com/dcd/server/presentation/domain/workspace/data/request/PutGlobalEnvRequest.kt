package com.dcd.server.presentation.domain.workspace.data.request

import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest

data class PutGlobalEnvRequest(
    val name: String,
    val description: String,
    val envList: List<PutEnvRequest>
)
