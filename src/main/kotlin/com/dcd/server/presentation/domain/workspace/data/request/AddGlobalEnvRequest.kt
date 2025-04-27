package com.dcd.server.presentation.domain.workspace.data.request

import com.dcd.server.presentation.domain.env.data.request.AddEnvRequest

data class AddGlobalEnvRequest(
    val envList: List<AddEnvRequest>
)
