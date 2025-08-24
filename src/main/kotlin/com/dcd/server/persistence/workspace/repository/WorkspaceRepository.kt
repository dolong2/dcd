package com.dcd.server.persistence.workspace.repository

import com.dcd.server.persistence.user.entity.UserJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkspaceRepository : JpaRepository<WorkspaceJpaEntity, UUID> {
    fun findAllByOwner(user: UserJpaEntity): List<WorkspaceJpaEntity>
    fun existsByTitle(title: String): Boolean
}