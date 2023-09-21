package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.core.domain.application.enums.DBType

class RunApplicationRequest(
    val langVersion: Int,
    val dbTypes: Array<DBType>?,
    val rootPassword: String?,
    val dataBaseName: String?
)