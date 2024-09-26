package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.dto.request.ExecuteCommandReqDto
import com.dcd.server.core.domain.application.dto.response.CommandResultResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.InvalidApplicationStatusException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class ExecuteCommandUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) {
    fun execute(applicationId: String, executeCommandReqDto: ExecuteCommandReqDto): CommandResultResDto {
        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        if (application.status != ApplicationStatus.RUNNING)
            throw InvalidApplicationStatusException()

        val result =
            commandPort.executeShellCommandWithResult("docker exec ${application.name.lowercase()} ${executeCommandReqDto.command}")

        return CommandResultResDto(result)
    }
}