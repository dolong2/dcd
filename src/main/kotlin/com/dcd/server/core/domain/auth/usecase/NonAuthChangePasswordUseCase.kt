package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.NonAuthChangePasswordReqDto
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class NonAuthChangePasswordUseCase(
    private val queryUserPort: QueryUserPort,
    private val commandUserPort: CommandUserPort,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(nonAuthChangePasswordReqDto: NonAuthChangePasswordReqDto) {
        val user = queryUserPort.findByEmail(nonAuthChangePasswordReqDto.email)
            ?: throw UserNotFoundException()

        val encodedPassword = passwordEncoder.encode(nonAuthChangePasswordReqDto.newPassword)
        val updatedUser = user.copy(password = encodedPassword)

        commandUserPort.save(updatedUser)
    }
}