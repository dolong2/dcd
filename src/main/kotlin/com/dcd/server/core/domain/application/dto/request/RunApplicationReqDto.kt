package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.application.enums.DBType

class RunApplicationReqDto(
    val langVersion: Int,
    val env: Map<String, String>
)
