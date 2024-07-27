package com.dcd.server.presentation.domain.workspace.data.request

data class AddGlobalEnvRequest(
    val envList: Map<String, String>
)
