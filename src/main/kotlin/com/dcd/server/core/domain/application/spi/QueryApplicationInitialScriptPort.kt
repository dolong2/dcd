package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.ApplicationInitialScript
import java.util.UUID

interface QueryApplicationInitialScriptPort {
    fun findById(id: UUID): ApplicationInitialScript?
    fun findAllByApplication(application: Application): List<ApplicationInitialScript>
}