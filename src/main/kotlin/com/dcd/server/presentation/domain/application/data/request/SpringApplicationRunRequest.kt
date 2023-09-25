package com.dcd.server.presentation.domain.application.data.request

import com.dcd.server.core.domain.application.enums.DBType

class SpringApplicationRunRequest(
    langVersion: Int,
    val dbTypes: Array<DBType>,
    val rootPassword: String? = null,
    val dataBaseName: String? = null
) : RunApplicationRequest(langVersion)