package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.response.TokenResponseDto
import com.dcd.server.presentation.domain.auth.data.response.ReissueTokenResponse
import com.dcd.server.presentation.domain.auth.data.response.SignInResponse

fun TokenResponseDto.toReissueResponse(): ReissueTokenResponse =
    ReissueTokenResponse(
        accessToken = this.accessToken,
        accessTokenExp = this.accessTokenExp,
        refreshToken = this.refreshToken,
        refreshTokenExp = this.refreshTokenExp
    )

fun TokenResponseDto.toResponse(): SignInResponse =
    SignInResponse(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        accessTokenExp = this.accessTokenExp,
        refreshTokenExp = this.refreshTokenExp
    )