package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest

data class PutApplicationEnvRequest(
    val envList: List<PutEnvRequest>
)