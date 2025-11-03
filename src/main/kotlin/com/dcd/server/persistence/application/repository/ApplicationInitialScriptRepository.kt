package com.dcd.server.persistence.application.repository

import com.dcd.server.persistence.application.entity.ApplicationInitialScriptJpaEntity
import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ApplicationInitialScriptRepository : JpaRepository<ApplicationInitialScriptJpaEntity, UUID> {
    fun findAllByApplication(applicationJpaEntity: ApplicationJpaEntity): List<ApplicationInitialScriptJpaEntity>
    fun deleteAllByApplication(applicationJpaEntity: ApplicationJpaEntity)
}