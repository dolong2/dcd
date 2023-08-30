package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.RefreshToken

interface QueryRefreshTokenPort {
    fun findByToken(token: String): RefreshToken?
    fun findByUserId(userId: String): List<RefreshToken>
}