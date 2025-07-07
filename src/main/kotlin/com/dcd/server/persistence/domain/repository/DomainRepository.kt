package com.dcd.server.persistence.domain.repository

import com.dcd.server.persistence.domain.entity.DomainJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DomainRepository : JpaRepository<DomainJpaEntity, UUID> {
}