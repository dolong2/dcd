package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.dto.response.TokenResDto

interface JwtPort {
    fun generateToken(userId: String): TokenResDto
}