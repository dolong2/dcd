package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.model.Role

interface JwtPort {
    fun generateToken(userId: String, roles: List<Role>): TokenResDto
}