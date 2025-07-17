package com.dcd.server.persistence.domain.repository

import com.dcd.server.persistence.domain.entity.DomainJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DomainRepository : JpaRepository<DomainJpaEntity, UUID> {
    fun existsByName(name: String): Boolean
    fun findAllByWorkspace(workspace: WorkspaceJpaEntity): List<DomainJpaEntity>
}