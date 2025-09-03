package com.dcd.server.persistence.volume.repository

import com.dcd.server.persistence.volume.entity.VolumeJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VolumeRepository : JpaRepository<VolumeJpaEntity, UUID> {
    fun existsByNameAndWorkspace(name: String, workspace: WorkspaceJpaEntity): Boolean
}