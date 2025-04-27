package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.presentation.domain.env.data.request.AddEnvRequest

data class AddApplicationEnvRequest(
    val envList: List<AddEnvRequest>
)