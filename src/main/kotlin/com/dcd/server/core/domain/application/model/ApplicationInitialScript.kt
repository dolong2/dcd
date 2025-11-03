package com.dcd.server.core.domain.application.model

import java.util.UUID

class ApplicationInitialScript(
    val id: UUID,
    val script: String,
    val application: Application
) {
}