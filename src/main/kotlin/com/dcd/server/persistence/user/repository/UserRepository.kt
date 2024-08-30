package com.dcd.server.persistence.user.repository

import com.dcd.server.core.domain.user.model.Status
import com.dcd.server.persistence.user.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserJpaEntity, String> {
    fun findByEmail(email: String): UserJpaEntity?
    fun existsByEmail(email: String): Boolean
    fun findAllByStatus(status: Status): List<UserJpaEntity>
}