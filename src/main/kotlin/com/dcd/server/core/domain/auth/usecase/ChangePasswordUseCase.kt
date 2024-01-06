package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.domain.auth.dto.request.PasswordChangeReqDto
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.user.spi.CommandUserPort
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class ChangePasswordUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val commandUserPort: CommandUserPort,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(passwordChangeReqDto: PasswordChangeReqDto) {
        val currentUser = getCurrentUserService.getCurrentUser()
        if (passwordEncoder.matches(passwordChangeReqDto.existingPassword, currentUser.password).not())
            throw PasswordNotCorrectException()

        val updatedUser = currentUser.copy(password = passwordEncoder.encode(passwordChangeReqDto.newPassword))

        commandUserPort.save(updatedUser)
    }
}