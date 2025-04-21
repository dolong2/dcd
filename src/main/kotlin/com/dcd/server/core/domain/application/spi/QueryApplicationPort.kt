package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.workspace.model.Workspace

interface QueryApplicationPort {
    fun findAllByWorkspace(workspace: Workspace, labels: List<String>? = null): List<Application>
    fun findById(id: String): Application?
    fun existsByExternalPort(externalPort: Int): Boolean
    fun findAllByStatus(status: ApplicationStatus): List<Application>
    fun existsByName(name: String): Boolean
    fun existsByNameAndWorkspace(name: String, workspace: Workspace): Boolean
}