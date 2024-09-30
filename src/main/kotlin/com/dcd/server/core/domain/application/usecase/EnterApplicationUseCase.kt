package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.AttachContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.presentation.domain.application.exception.InvalidConnectionInfoException
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@ReadOnlyUseCase
class EnterApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val attachContainerService: AttachContainerService,
    private val parseTokenAdapter: ParseTokenAdapter
) {
    fun execute(applicationId: String, session: WebSocketSession, cmd: String) {
        val accessToken = (session.attributes["accessToken"] as? String
            ?: throw InvalidConnectionInfoException("세션에 인증 정보가 존재하지 않음", CloseStatus.PROTOCOL_ERROR))

        val userId = parseTokenAdapter.getAuthentication(accessToken).name

        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        if (userId != application.workspace.owner.id)
            throw WorkspaceOwnerNotSameException()

        attachContainerService.attachService(application, session, cmd)
    }
}