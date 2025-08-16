package com.dcd.server.core.domain.env.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.model.ApplicationEnvMatcher
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

interface QueryApplicationEnvPort {
    fun findById(id: UUID): ApplicationEnv?

    fun findByKeyAndApplication(key: String, application: Application): ApplicationEnvDetail?

    fun findByApplication(application: Application): List<ApplicationEnv>

    fun findAllByWorkspace(workspace: Workspace): List<ApplicationEnv>

    fun findAllByLabelsIn(workspace: Workspace, labels: List<String>): List<ApplicationEnv>

    fun findAllMatcherByEnv(applicationEnv: ApplicationEnv): List<ApplicationEnvMatcher>
}