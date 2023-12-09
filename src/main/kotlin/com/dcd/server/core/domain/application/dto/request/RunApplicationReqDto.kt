package com.dcd.server.core.domain.application.dto.request


class RunApplicationReqDto(
    val langVersion: Int,
    val env: Map<String, String>
)
