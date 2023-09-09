package com.dcd.server.persistence.application.repository

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.user.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ApplicationRepository : JpaRepository<ApplicationJpaEntity, String> {
    fun findAllByOwner(owner: UserJpaEntity): List<ApplicationJpaEntity>
}