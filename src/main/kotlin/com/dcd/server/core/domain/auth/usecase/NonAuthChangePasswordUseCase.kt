package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.NonAuthChangePasswordReqDto
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.user.spi.CommandUserPort
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class NonAuthChangePasswordUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val commandUserPort: CommandUserPort,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(nonAuthChangePasswordReqDto: NonAuthChangePasswordReqDto) {
        val user = getCurrentUserService.getCurrentUser()

        val encodedPassword = passwordEncoder.encode(nonAuthChangePasswordReqDto.newPassword)
        val updatedUser = user.copy(password = encodedPassword)

        commandUserPort.save(updatedUser)
    }
}