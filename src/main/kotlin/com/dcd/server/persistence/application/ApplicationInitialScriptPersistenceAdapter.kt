package com.dcd.server.persistence.application

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.ApplicationInitialScript
import com.dcd.server.core.domain.application.spi.ApplicationInitialScriptPort
import com.dcd.server.persistence.application.adapter.toDomain
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.application.repository.ApplicationInitialScriptRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ApplicationInitialScriptPersistenceAdapter(
    private val applicationInitialScriptRepository: ApplicationInitialScriptRepository
) : ApplicationInitialScriptPort{
    override fun save(applicationInitialScript: ApplicationInitialScript) {
        applicationInitialScriptRepository.save(applicationInitialScript.toEntity())
    }

    override fun saveAll(applicationInitialScriptList: List<ApplicationInitialScript>) {
        applicationInitialScriptRepository.saveAll(applicationInitialScriptList.map { it.toEntity() })
    }

    override fun delete(applicationInitialScript: ApplicationInitialScript) {
        applicationInitialScriptRepository.delete(applicationInitialScript.toEntity())
    }

    override fun deleteAll(applicationInitialScriptList: List<ApplicationInitialScript>) {
        applicationInitialScriptRepository.deleteAll(applicationInitialScriptList.map { it.toEntity() })
    }

    override fun deleteByApplication(application: Application) {
        applicationInitialScriptRepository.deleteAllByApplication(application.toEntity())
    }

    override fun findById(id: UUID): ApplicationInitialScript? =
        applicationInitialScriptRepository.findByIdOrNull(id)
            ?.toDomain()

    override fun findAllByApplication(application: Application): List<ApplicationInitialScript> =
        applicationInitialScriptRepository.findAllByApplication(application.toEntity())
            .map { it.toDomain() }
}