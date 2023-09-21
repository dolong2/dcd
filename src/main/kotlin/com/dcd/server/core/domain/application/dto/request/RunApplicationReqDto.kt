package com.dcd.server.core.domain.application.dto.request

import com.dcd.server.core.domain.application.enums.DBType

class RunApplicationReqDto(
    val langVersion: Int,
    val dbTypes: Array<DBType>?,
    val rootPassword: String?,
    val dataBaseName: String?
)
