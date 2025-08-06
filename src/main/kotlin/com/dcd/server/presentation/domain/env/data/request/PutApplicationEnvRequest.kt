package com.dcd.server.presentation.domain.env.data.request

data class PutApplicationEnvRequest(
    val name: String,
    val description: String,
    val details: List<PutEnvRequest>,
    val applicationIdList: List<String>?,
    val applicationLabelList: List<String>?,
)
