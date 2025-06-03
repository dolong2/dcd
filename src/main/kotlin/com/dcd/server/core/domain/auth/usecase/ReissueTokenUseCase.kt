package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.ExpiredRefreshTokenException
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.spi.*
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.infrastructure.global.jwt.adapter.JwtPrefix
import com.dcd.server.infrastructure.global.jwt.exception.TokenTypeNotValidException

@UseCase
class ReissueTokenUseCase(
    private val queryRefreshTokenPort: QueryRefreshTokenPort,
    private val commandRefreshTokenPort: CommandRefreshTokenPort,
    private val generateTokenPort: GenerateTokenPort,
    private val parseTokenPort: ParseTokenPort,
    private val queryUserPort: QueryUserPort
) {
    fun execute(token: String): TokenResDto {
        val jwtType = parseTokenPort.getJwtType(token)
        if (jwtType != JwtPrefix.REFRESH)
            throw TokenTypeNotValidException()

        val refreshToken = (queryRefreshTokenPort.findByToken(token)
            ?: throw ExpiredRefreshTokenException()) // 리프레시 토큰 만료
        val user = (queryUserPort.findById(refreshToken.userId)
            ?: throw UserNotFoundException())
        commandRefreshTokenPort.delete(refreshToken)
        return generateTokenPort.generateToken(user.id)
    }
}