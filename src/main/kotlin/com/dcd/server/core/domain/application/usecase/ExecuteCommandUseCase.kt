package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.dto.request.ExecuteCommandReqDto
import com.dcd.server.core.domain.application.dto.response.CommandResultResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.InvalidApplicationStatusException
import com.dcd.server.core.domain.application.exception.InvalidCmdException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ExecContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.presentation.domain.application.exception.InvalidConnectionInfoException
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@UseCase
class ExecuteCommandUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val execContainerService: ExecContainerService,
    private val parseTokenAdapter: ParseTokenAdapter,
    private val commandPort: CommandPort
) {
    fun execute(applicationId: String, executeCommandReqDto: ExecuteCommandReqDto): CommandResultResDto {
        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        if (application.status != ApplicationStatus.RUNNING)
            throw InvalidApplicationStatusException()

        val result =
            commandPort.executeShellCommandWithResult("docker exec ${application.name.lowercase()} sh -c 'cd / && ${executeCommandReqDto.command}'")

        return CommandResultResDto(result)
    }

    fun execute(applicationId: String, session: WebSocketSession, cmd: String) {
        val accessToken = (session.attributes["accessToken"] as? String
            ?: throw InvalidConnectionInfoException("세션에 인증 정보가 존재하지 않음", CloseStatus.PROTOCOL_ERROR))

        val userId = parseTokenAdapter.getAuthentication(accessToken).name

        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        if (application.status != ApplicationStatus.RUNNING)
            throw InvalidApplicationStatusException()

        if (userId != application.workspace.owner.id)
            throw WorkspaceOwnerNotSameException()

        execContainerService.execCmd(application, session, cmd)
    }

    private fun validateCmd(cmd: String) {
        val forbiddenPatterns = listOf(";", "`", "$")
        if (cmd.length > 100)
            throw InvalidCmdException()
        else if (forbiddenPatterns.any { cmd.contains(it) })
            throw InvalidCmdException()
    }
}