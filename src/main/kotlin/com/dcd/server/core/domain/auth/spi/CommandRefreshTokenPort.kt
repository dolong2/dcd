package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.RefreshToken

interface CommandRefreshTokenPort {
    fun save(refreshToken: RefreshToken)
    fun delete(refreshToken: RefreshToken)
}