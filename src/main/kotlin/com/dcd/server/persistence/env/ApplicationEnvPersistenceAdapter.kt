package com.dcd.server.persistence.env

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.model.ApplicationEnvMatcher
import com.dcd.server.core.domain.env.spi.ApplicationEnvPort
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.env.adapter.toDomain
import com.dcd.server.persistence.env.adapter.toEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvDetailEntity
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

    override fun save(applicationEnv: ApplicationEnv) {
        applicationEnvRepository.save(applicationEnv.toEntity())
    }

    override fun saveAllMatcher(applicationEnvMatcher: List<ApplicationEnvMatcher>) {
        applicationEnvMatcherRepository.saveAll(applicationEnvMatcher.map { it.toEntity() })
    }

    override fun save(applicationEnv: ApplicationEnv, application: Application) {
        val applicationEnvEntity = applicationEnv.toEntity()
        val applicationEnvMatcherEntity = ApplicationEnvMatcherEntity(
            application = application.toEntity(),
            applicationEnv = applicationEnvEntity
        )
        applicationEnvRepository.save(applicationEnvEntity)
        applicationEnvMatcherRepository.save(applicationEnvMatcherEntity)
        applicationEnvDetailRepository.saveAll(applicationEnv.details.map { it.toEntity(applicationEnvEntity) })
    }

    override fun saveAll(applicationEnvList: List<ApplicationEnv>, application: Application) {
        val applicationEntity = application.toEntity()

        val applicationEnvDetailList = mutableListOf<ApplicationEnvDetailEntity>()
        val applicationEnvMatcherEntityList = mutableListOf<ApplicationEnvMatcherEntity>()

        val applicationEntityList = applicationEnvList.map {
            val applicationEnvEntity = it.toEntity()

            val detailList = it.details.map { envDetail -> envDetail.toEntity(applicationEnvEntity) }
            applicationEnvDetailList.addAll(detailList)

            applicationEnvMatcherEntityList.add(
                ApplicationEnvMatcherEntity(
                    application = applicationEntity,
                    applicationEnv = applicationEnvEntity
                )
            )

            applicationEnvEntity
        }
        applicationEnvRepository.saveAll(applicationEntityList)
        applicationEnvMatcherRepository.saveAll(applicationEnvMatcherEntityList)
        applicationEnvDetailRepository.saveAll(applicationEnvDetailList)
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
        applicationEnvDetailRepository.deleteById(applicationEnvDetail.id)
}