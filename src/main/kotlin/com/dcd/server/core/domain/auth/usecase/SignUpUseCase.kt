package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.dto.extension.toEntity
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.exception.AlreadyExistsUserException
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort

@UseCase
class SignUpUseCase(
    private val securityService: SecurityService,
    private val commandUserPort: CommandUserPort,
    private val queryUserPort: QueryUserPort
) {
    fun execute(signUpReqDto: SignUpReqDto) {
        if(queryUserPort.existsByEmail(signUpReqDto.email))
            throw AlreadyExistsUserException()
        val encodePassword = securityService.encodePassword(signUpReqDto.password)
        val user = signUpReqDto.toEntity(encodePassword)
        commandUserPort.save(user)
    }
}