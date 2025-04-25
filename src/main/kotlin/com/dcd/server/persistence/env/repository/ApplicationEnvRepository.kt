package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ApplicationEnvRepository : JpaRepository<ApplicationEnvEntity, UUID> {
}