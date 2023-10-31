package com.dcd.server.core.domain.workspace.spi

import com.dcd.server.core.domain.workspace.model.Workspace

interface CommandWorkspacePort {
    fun save(workspace: Workspace)
    fun delete(workspace: Workspace)
}