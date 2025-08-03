package com.dcd.server.persistence.env.repository

import com.dcd.server.persistence.env.entity.GlobalEnvEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GlobalEnvRepository : JpaRepository<GlobalEnvEntity, UUID> {
}