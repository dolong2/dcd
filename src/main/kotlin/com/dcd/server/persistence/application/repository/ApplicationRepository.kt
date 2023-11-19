package com.dcd.server.persistence.application.repository

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ApplicationRepository : JpaRepository<ApplicationJpaEntity, String> {
    fun findAllByWorkspace(workspace: WorkspaceJpaEntity): List<ApplicationJpaEntity>
}