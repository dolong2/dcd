package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.ApplicationInitialScript

interface CommandApplicationInitialScriptPort {
    fun save(applicationInitialScript: ApplicationInitialScript)
    fun saveAll(applicationInitialScriptList: List<ApplicationInitialScript>)
    fun delete(applicationInitialScript: ApplicationInitialScript)
    fun deleteAll(applicationInitialScriptList: List<ApplicationInitialScript>)
    fun deleteByApplication(application: Application)
}