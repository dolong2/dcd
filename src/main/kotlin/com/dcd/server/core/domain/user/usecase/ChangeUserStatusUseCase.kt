package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort

@UseCase
class ChangeUserStatusUseCase(
    private val queryUserPort: QueryUserPort,
    private val commandUserPort: CommandUserPort,
) {
    fun execute(userId: String, status: Status) {
        val user = (queryUserPort.findById(userId)
            ?: throw UserNotFoundException())

        val updatedUser = user.copy(status = status)
        commandUserPort.save(updatedUser)
    }
}