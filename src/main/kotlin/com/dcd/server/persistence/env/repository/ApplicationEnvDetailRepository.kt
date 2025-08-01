package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvDetailEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ApplicationEnvDetailRepository : JpaRepository<ApplicationEnvDetailEntity, UUID> {
    @Query("select d from ApplicationEnvDetailEntity d where d.key = :key and (select count(m) from ApplicationEnvMatcherEntity m where m.application = :application and m.applicationEnv = d) > 0")
    fun findByKeyAndApplication(key: String, application: ApplicationJpaEntity): ApplicationEnvDetailEntity?
}