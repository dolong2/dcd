package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.presentation.domain.auth.data.response.ReissueTokenResponse
import com.dcd.server.presentation.domain.auth.data.response.SignInResponse

fun TokenResDto.toReissueResponse(): ReissueTokenResponse =
    ReissueTokenResponse(
        accessToken = this.accessToken,
        accessTokenExp = this.accessTokenExp,
        refreshToken = this.refreshToken,
        refreshTokenExp = this.refreshTokenExp
    )

fun TokenResDto.toResponse(): SignInResponse =
    SignInResponse(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        accessTokenExp = this.accessTokenExp,
        refreshTokenExp = this.refreshTokenExp
    )