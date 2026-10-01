package com.dcd.server.persistence.domain

import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.domain.spi.DomainPort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.common.extension.toUUIDOrNull
import com.dcd.server.persistence.domain.adapter.toDomain
import com.dcd.server.persistence.domain.adapter.toEntity
import com.dcd.server.persistence.domain.repository.DomainRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class DomainPersistenceAdapter(
    private val domainRepository: DomainRepository
) : DomainPort {
    override fun save(domain: Domain) {
        domainRepository.save(domain.toEntity())
    }

    override fun delete(domain: Domain) {
        domainRepository.delete(domain.toEntity())
    }

    override fun saveAll(domainList: List<Domain>) {
        domainRepository.saveAll(domainList.map { it.toEntity() })
    }

    override fun findAll(): List<Domain> =
        domainRepository.findAll()
            .map { it.toDomain() }

    override fun findById(id: String): Domain? =
        id.toUUIDOrNull()
            ?.let { domainRepository.findByIdOrNull(it) }
            ?.toDomain()

    override fun findByWorkspace(workspace: Workspace): List<Domain> =
        domainRepository.findAllByWorkspace(workspace.toEntity())
            .map { it.toDomain() }

    override fun existsByName(name: String): Boolean =
        domainRepository.existsByName(name)
}