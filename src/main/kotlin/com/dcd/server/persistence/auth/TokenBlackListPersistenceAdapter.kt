package com.dcd.server.persistence.auth

import com.dcd.server.core.domain.auth.model.TokenBlackList
import com.dcd.server.core.domain.auth.spi.TokenBlackListPort
import com.dcd.server.persistence.auth.adapter.toEntity
import com.dcd.server.persistence.auth.repository.TokenBlackListRepository
import org.springframework.stereotype.Component

@Component
class TokenBlackListPersistenceAdapter(
    private val tokenBlackListRepository: TokenBlackListRepository
) : TokenBlackListPort {
    override fun save(tokenBlackList: TokenBlackList) {
        tokenBlackListRepository.save(tokenBlackList.toEntity())
    }

    override fun existsByToken(token: String): Boolean =
        tokenBlackListRepository.existsById(token)
}