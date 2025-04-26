package com.dcd.server.persistence.env

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.spi.ApplicationEnvPort
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.env.adapter.toDomain
import com.dcd.server.persistence.env.adapter.toEntity
import com.dcd.server.persistence.env.repository.ApplicationEnvRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.List

@Component
class ApplicationEnvPersistenceAdapter(
    private val applicationEnvRepository: ApplicationEnvRepository
) : ApplicationEnvPort {
    override fun findById(id: UUID): ApplicationEnv? =
        applicationEnvRepository.findByIdOrNull(id)?.toDomain()

    override fun findByKeyAndApplication(key: String, application: Application): ApplicationEnv? =
        applicationEnvRepository.findByApplicationAndKey(application.toEntity(), key)?.toDomain()

    override fun save(applicationEnv: ApplicationEnv, application: Application) {
        val applicationEnvEntity = applicationEnv.toEntity(application)
        applicationEnvRepository.save(applicationEnvEntity)
    }

    override fun saveAll(applicationEnvList: List<ApplicationEnv>, application: Application) {
        val applicationEntityList = applicationEnvList.map { it.toEntity(application) }
        applicationEnvRepository.saveAll(applicationEntityList)
    }

    override fun delete(applicationEnv: ApplicationEnv) {
        applicationEnvRepository.deleteById(applicationEnv.id)
    }

    override fun deleteAll(applicationEnvList: List<ApplicationEnv>) {
        applicationEnvRepository.deleteAllById(applicationEnvList.map { it.id })
    }
}