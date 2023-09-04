package com.dcd.server.core.domain.auth.dto.response

import java.time.LocalDateTime

data class TokenResponseDto(
    val accessToken: String,

    val accessTokenExp: LocalDateTime,

    val refreshToken: String,

    val refreshTokenExp: LocalDateTime
)