package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.core.domain.application.enums.DBType

class RunApplicationRequest(
    val langVersion: Int,
    val dbTypes: Array<DBType>? = null,
    val rootPassword: String? = null,
    val dataBaseName: String? = null
)