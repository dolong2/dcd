package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort

@UseCase
class SignOutUseCase(
    private val commandRefreshTokenPort: CommandRefreshTokenPort,
    private val queryRefreshTokenPort: QueryRefreshTokenPort,
    private val securityService: SecurityService
) {
    fun execute() {
        val userId = securityService.getCurrentUserId()
        val refreshTokenList = queryRefreshTokenPort.findByUserId(userId)
        commandRefreshTokenPort.delete(refreshTokenList)
    }
}