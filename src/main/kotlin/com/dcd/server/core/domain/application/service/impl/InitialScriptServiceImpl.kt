package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.ApplicationInitialScript
import com.dcd.server.core.domain.application.service.InitialScriptService
import com.dcd.server.core.domain.application.spi.CommandApplicationInitialScriptPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InitialScriptServiceImpl(
    private val commandApplicationInitialScriptPort: CommandApplicationInitialScriptPort
) : InitialScriptService {
    override fun write(
        application: Application,
        initialScripts: List<String>,
    ) {
        commandApplicationInitialScriptPort.deleteByApplication(application)

        val initialScriptList = initialScripts.map { script ->
            ApplicationInitialScript(
                id = UUID.randomUUID(),
                script = script,
                application = application,
            )
        }
        commandApplicationInitialScriptPort.saveAll(initialScriptList)
    }
}