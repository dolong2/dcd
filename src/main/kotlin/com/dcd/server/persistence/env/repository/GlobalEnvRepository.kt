package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.env.entity.GlobalEnvEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GlobalEnvRepository : JpaRepository<GlobalEnvEntity, UUID> {
    fun findByWorkspaceAndKey(workspace: WorkspaceJpaEntity, key: String): GlobalEnvEntity?
}