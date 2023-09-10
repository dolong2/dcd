package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.application.dto.extenstion.toEntity
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.spi.QueryUserPort

@UseCase
class CreateApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val securityService: SecurityService,
    private val queryUserPort: QueryUserPort
) {
    fun execute(createApplicationReqDto: CreateApplicationReqDto) {
        val userId = securityService.getCurrentUserId()
        val user = (queryUserPort.findById(userId)
            ?: throw UserNotFoundException())
        val application = createApplicationReqDto.toEntity(user)
        commandApplicationPort.save(application)
    }
}