package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvMatcherEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ApplicationEnvMatcherRepository : JpaRepository<ApplicationEnvMatcherEntity, UUID> {
    fun deleteByApplicationEnv(applicationEnv: ApplicationEnvEntity)
    fun deleteByApplicationEnvIn(applicationEnvList: List<ApplicationEnvEntity>)
    fun findByApplication(application: ApplicationJpaEntity): List<ApplicationEnvMatcherEntity>
    fun findAllByApplicationEnv(applicationEnv: ApplicationEnvEntity): List<ApplicationEnvMatcherEntity>
}