package com.dcd.server.core.domain.workspace.spi

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace

interface QueryWorkspacePort {
    fun findById(id: String): Workspace?
    fun findAll(): List<Workspace>

    fun findByUser(user: User): List<Workspace>
}