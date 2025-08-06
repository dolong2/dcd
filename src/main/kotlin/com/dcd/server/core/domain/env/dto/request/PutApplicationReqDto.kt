package com.dcd.server.core.domain.env.dto.request

data class PutApplicationReqDto(
    val name: String,
    val description: String,
    val details: List<PutEnvReqDto>,
    val applicationIdList: List<String>?,
    val applicationLabelList: List<String>?,
)
