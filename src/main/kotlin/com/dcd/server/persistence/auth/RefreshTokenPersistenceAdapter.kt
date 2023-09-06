package com.dcd.server.persistence.auth

import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.spi.RefreshTokenPort
import com.dcd.server.persistence.auth.adapter.toDomain
import com.dcd.server.persistence.auth.adapter.toEntity
import com.dcd.server.persistence.auth.repository.RefreshTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository
) : RefreshTokenPort {
    override fun save(refreshToken: RefreshToken) {
        refreshTokenRepository.save(refreshToken.toEntity())
    }

    override fun delete(refreshToken: RefreshToken) {
        refreshTokenRepository.delete(refreshToken.toEntity())
    }

    override fun findByToken(token: String): RefreshToken? =
        refreshTokenRepository.findByIdOrNull(token)
            ?.toDomain()

    override fun findByUserId(userId: String): List<RefreshToken> =
        refreshTokenRepository.findByUserId(userId)
            .map { it.toDomain() }
}