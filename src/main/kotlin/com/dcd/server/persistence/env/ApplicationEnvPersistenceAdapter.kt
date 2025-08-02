package com.dcd.server.persistence.env

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.ApplicationEnvPort
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.env.adapter.toDomain
import com.dcd.server.persistence.env.adapter.toEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvMatcherEntity
import com.dcd.server.persistence.env.repository.ApplicationEnvDetailRepository
import com.dcd.server.persistence.env.repository.ApplicationEnvMatcherRepository
import com.dcd.server.persistence.env.repository.ApplicationEnvRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.List

@Component
class ApplicationEnvPersistenceAdapter(
    private val applicationEnvRepository: ApplicationEnvRepository,
    private val applicationEnvMatcherRepository: ApplicationEnvMatcherRepository,
    private val applicationEnvDetailRepository: ApplicationEnvDetailRepository
) : ApplicationEnvPort {
    override fun findById(id: UUID): ApplicationEnv? =
        applicationEnvRepository.findByIdOrNull(id)?.toDomain()

    override fun findByKeyAndApplication(key: String, application: Application): ApplicationEnvDetail? =
        applicationEnvDetailRepository.findByKeyAndApplication(key, application.toEntity())?.toDomain()

    override fun findByApplication(application: Application): List<ApplicationEnv> =
        applicationEnvMatcherRepository.findByApplication(application.toEntity())
            .map { it.applicationEnv }
            .map { it.toDomain() }

    override fun save(applicationEnv: ApplicationEnv, application: Application) {
        val applicationEnvEntity = applicationEnv.toEntity()
        val applicationEnvMatcherEntity = ApplicationEnvMatcherEntity(
            application = application.toEntity(),
            applicationEnv = applicationEnvEntity
        )
        applicationEnvRepository.save(applicationEnvEntity)
        applicationEnvMatcherRepository.save(applicationEnvMatcherEntity)
        applicationEnvDetailRepository.saveAll(applicationEnv.details.map { it.toEntity() })
    }

    override fun saveAll(applicationEnvList: List<ApplicationEnv>, application: Application) {
        val applicationEntity = application.toEntity()

        val applicationEntityList = applicationEnvList.map { it.toEntity() }
        applicationEnvRepository.saveAll(applicationEntityList)

        val applicationEnvMatcherEntityList = applicationEntityList.map {
            ApplicationEnvMatcherEntity(
                application = applicationEntity,
                applicationEnv = it
            )
        }
        applicationEnvMatcherRepository.saveAll(applicationEnvMatcherEntityList)
    }

    override fun delete(applicationEnv: ApplicationEnv) {
        applicationEnvMatcherRepository.deleteByApplicationEnv(applicationEnv.toEntity())
        applicationEnvRepository.deleteById(applicationEnv.id)
    }

    override fun deleteAll(applicationEnvList: List<ApplicationEnv>) {
        val applicationEnvEntityList = applicationEnvList.map { it.toEntity() }
        applicationEnvMatcherRepository.deleteByApplicationEnvIn(applicationEnvEntityList)
        applicationEnvRepository.deleteAll(applicationEnvEntityList)
    }

    override fun deleteDetail(applicationEnvDetail: ApplicationEnvDetail) =
        applicationEnvDetailRepository.delete(applicationEnvDetail.toEntity())
}