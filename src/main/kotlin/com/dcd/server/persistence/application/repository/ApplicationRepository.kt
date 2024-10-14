package com.dcd.server.persistence.application.repository

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ApplicationRepository : JpaRepository<ApplicationJpaEntity, String> {
    fun findAllByWorkspace(workspace: WorkspaceJpaEntity): List<ApplicationJpaEntity>
    @Query("select app from ApplicationJpaEntity app where app.workspace = :workspace and app.labels IN :labels")
    fun findAllByWorkspaceAndLabels(
        @Param("workspace") workspace: WorkspaceJpaEntity,
        @Param("labels") labels: List<String>
    ): List<ApplicationJpaEntity>
    fun existsByExternalPort(externalPort: Int): Boolean
    fun findAllByStatus(status: ApplicationStatus): List<ApplicationJpaEntity>
}