package com.dcd.server.persistence.auth.repository

import com.dcd.server.persistence.auth.entity.RefreshTokenEntity
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshTokenEntity, String>{
    fun findByUserId(userId: String): List<RefreshTokenEntity>
}