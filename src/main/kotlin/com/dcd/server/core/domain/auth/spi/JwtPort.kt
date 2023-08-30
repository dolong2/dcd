package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.dto.response.TokenResponseData
import com.dcd.server.core.domain.auth.model.Role

interface JwtPort {
    fun generateToken(userId: String, role: Role): TokenResponseData
}