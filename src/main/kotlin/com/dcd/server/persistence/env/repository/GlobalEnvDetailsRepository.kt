package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.env.entity.GlobalEnvDetailEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface GlobalEnvDetailsRepository : JpaRepository<GlobalEnvDetailEntity, UUID> {
    @Query("select d from GlobalEnvDetailEntity d where d.envDetail.key = :key and exists(select g from GlobalEnvEntity g where g.workspace = :workspace)")
    fun findByWorkspaceAndKey(workspace: WorkspaceJpaEntity, key: String): GlobalEnvDetailEntity?
}