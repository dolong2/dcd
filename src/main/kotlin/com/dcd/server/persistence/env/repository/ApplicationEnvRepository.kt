package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ApplicationEnvRepository : JpaRepository<ApplicationEnvEntity, UUID> {
    fun findAllByWorkspace(workspaceJpaEntity: WorkspaceJpaEntity): List<ApplicationEnvEntity>

    fun findAllByLabelsIn(labels: List<String>): List<ApplicationEnvEntity>
}