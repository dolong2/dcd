package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.model.TokenBlackList
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.CommandTokenBlackListPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort

@UseCase
class SignOutUseCase(
    private val commandRefreshTokenPort: CommandRefreshTokenPort,
    private val queryRefreshTokenPort: QueryRefreshTokenPort,
    private val securityService: SecurityService,
    private val commandTokenBlackListPort: CommandTokenBlackListPort
) {
    fun execute(accessToken: String) {
        val userId = securityService.getCurrentUserId()
        val refreshTokenList = queryRefreshTokenPort.findByUserId(userId)
        commandTokenBlackListPort.save(TokenBlackList(accessToken.replace("Bearer ", ""), 60 * 10))
        commandRefreshTokenPort.delete(refreshTokenList)
    }
}