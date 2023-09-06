package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.response.TokenResponseDto
import com.dcd.server.presentation.domain.auth.data.response.ReissueTokenResponse

fun TokenResponseDto.toReissueResponse(): ReissueTokenResponse =
    ReissueTokenResponse(
        accessToken = this.accessToken,
        accessTokenExp = this.accessTokenExp,
        refreshToken = this.refreshToken,
        refreshTokenExp = this.refreshTokenExp
    )