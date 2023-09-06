package com.dcd.server.presentation.domain.auth.data.response

import java.time.LocalDateTime

data class ReissueTokenResponse(
    val accessToken: String,
    val accessTokenExp: LocalDateTime,
    val refreshToken: String,
    val refreshTokenExp: LocalDateTime
)