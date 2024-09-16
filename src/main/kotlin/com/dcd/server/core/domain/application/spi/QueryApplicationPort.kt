package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace

interface QueryApplicationPort {
    fun findAllByWorkspace(workspace: Workspace): List<Application>
    fun findById(id: String): Application?
    fun existsByExternalPort(externalPort: Int): Boolean
    fun findAllByStatus(status: ApplicationStatus): List<Application>
}