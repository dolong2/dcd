package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.dto.request.SignInReqDto
import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.core.domain.user.spi.QueryUserPort

@UseCase
class SignInUseCase(
    private val queryUserPort: QueryUserPort,
    private val securityService: SecurityService,
    private val jwtPort: JwtPort
) {
    fun execute(signInReqDto: SignInReqDto): TokenResDto {
        val user = (queryUserPort.findByEmail(signInReqDto.email)
            ?: throw UserNotFoundException()) // 해당 유저를 찾을 수 없음
        securityService.matchPassword(signInReqDto.password, user.password)
        return jwtPort.generateToken(user.id)
    }
}