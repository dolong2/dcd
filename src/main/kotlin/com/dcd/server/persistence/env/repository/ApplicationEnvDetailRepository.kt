package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvDetailEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ApplicationEnvDetailRepository : JpaRepository<ApplicationEnvDetailEntity, UUID> {
    @Query("select d from ApplicationEnvDetailEntity d where d.envDetail.key = :key and exists(select m from ApplicationEnvMatcherEntity m where m.application = :application and m.applicationEnv = d.applicationEnv)")
    fun findByKeyAndApplication(key: String, application: ApplicationJpaEntity): ApplicationEnvDetailEntity?
    fun deleteAllByApplicationEnv(applicationEnvEntity: ApplicationEnvEntity)
}