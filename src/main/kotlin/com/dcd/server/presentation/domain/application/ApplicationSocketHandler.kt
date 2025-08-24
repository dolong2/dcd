package com.dcd.server.presentation.domain.application

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.socket.SocketHandler
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.usecase.ExecuteCommandUseCase
import com.dcd.server.presentation.domain.application.exception.InvalidConnectionInfoException
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

@Component
class ApplicationSocketHandler(
    private val executeCommandUseCase: ExecuteCommandUseCase
) : SocketHandler() {
    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val applicationId = (session.attributes["applicationId"] as? String
            ?: throw ApplicationNotFoundException())

        val cmd = message.payload

        try {
            // Docker attach API를 사용하여 컨테이너에 접속
            executeCommandUseCase.execute(applicationId, session, cmd)
        } catch (ex: Exception) {
            handleTransportError(session, ex)
        }
    }

    override fun handleTransportError(session: WebSocketSession, ex: Throwable) {
        val closeStatus = when (ex) {
            is InvalidConnectionInfoException -> {
                session.sendMessage(TextMessage(ex.message!!))
                ex.closeStatus
            }

            is BasicException -> {
                val errorCode = ex.errorCode
                session.sendMessage(TextMessage(errorCode.msg))
                CloseStatus.BAD_DATA
            }

            else -> {
                if (session.isOpen)
                    session.sendMessage(TextMessage(ex.message ?: "서버 내부 에러"))
                CloseStatus.SERVER_ERROR
            }
        }

        session.close(closeStatus)
    }
}