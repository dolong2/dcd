package com.dcd.server.persistence.application

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.ApplicationPort
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.application.adapter.toDomain
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.application.repository.ApplicationRepository
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.workspace.adapter.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ApplicationPersistenceAdapter(
    private val applicationRepository: ApplicationRepository
) : ApplicationPort {
    override fun save(application: Application) {
        applicationRepository.save(application.toEntity())
    }

    override fun delete(application: Application) {
        applicationRepository.delete(application.toEntity())
    }

    override fun saveAll(applicationList: List<Application>) {
        applicationRepository.saveAll(applicationList.map { it.toEntity() })
    }

    override fun findAllByWorkspace(workspace: Workspace): List<Application> =
        applicationRepository.findAllByWorkspace(workspace.toEntity())
            .map { it.toDomain() }

    override fun findById(id: String): Application? =
        applicationRepository.findByIdOrNull(id)
            ?.toDomain()

    override fun existsByExternalPort(externalPort: Int): Boolean =
        applicationRepository.existsByExternalPort(externalPort)

    override fun findAllByStatus(status: ApplicationStatus): List<Application> =
        applicationRepository.findAllByStatus(status)
            .map { it.toDomain() }
}