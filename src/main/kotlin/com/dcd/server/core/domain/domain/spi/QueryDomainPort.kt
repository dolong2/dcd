package com.dcd.server.core.domain.domain.spi

import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.workspace.model.Workspace

interface QueryDomainPort {
    fun findAll(): List<Domain>
    fun findById(id: String): Domain?
    fun findByWorkspace(workspace: Workspace): List<Domain>
    fun existsByName(name: String): Boolean
}