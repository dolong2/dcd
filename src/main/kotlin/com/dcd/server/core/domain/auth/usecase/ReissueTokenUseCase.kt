package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.ExpiredRefreshTokenException
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort
import com.dcd.server.core.domain.user.spi.QueryUserPort

@UseCase
class ReissueTokenUseCase(
    private val queryRefreshTokenPort: QueryRefreshTokenPort,
    private val commandRefreshTokenPort: CommandRefreshTokenPort,
    private val jwtPort: JwtPort,
    private val queryUserPort: QueryUserPort
) {
    fun execute(token: String): TokenResDto {
        val refreshToken = (queryRefreshTokenPort.findByToken(token)
            ?: throw ExpiredRefreshTokenException()) // 리프레시 토큰 만료
        val user = (queryUserPort.findById(refreshToken.userId)
            ?: throw UserNotFoundException())
        commandRefreshTokenPort.delete(refreshToken)
        return jwtPort.generateToken(user.id, user.roles)
    }
}