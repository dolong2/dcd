package com.dcd.server.core.domain.env.service.impl

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnvMatcher
import com.dcd.server.core.domain.env.service.EnvAutoMatchService
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.model.Workspace
import org.springframework.stereotype.Service

@Service
class EnvAutoMatchServiceImpl(
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val commandApplicationEnvPort: CommandApplicationEnvPort
) : EnvAutoMatchService {
    override fun match(workspace: Workspace, application: Application) {
        val applicationEnvList = queryApplicationEnvPort.findAllByLabelsIn(workspace, application.labels)

        val envMatchers =
            applicationEnvList.map { env ->
                ApplicationEnvMatcher(
                    application = application,
                    applicationEnv = env
                )
            }

        commandApplicationEnvPort.saveAllMatcher(envMatchers)
    }
}