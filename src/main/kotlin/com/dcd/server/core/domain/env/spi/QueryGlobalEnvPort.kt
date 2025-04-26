package com.dcd.server.core.domain.env.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.*

interface QueryGlobalEnvPort {
    fun findById(id: UUID): GlobalEnv?

    fun findByKeyAndWorkspace(key: String, workspace: Workspace): GlobalEnv?
}