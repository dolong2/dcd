package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.exception.ContainerNotConnectedException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.ExecContainerService
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.*
import java.util.concurrent.ConcurrentHashMap

@Service
class ExecContainerServiceImpl(
    private val containerPort: ContainerPort,
    private val commandPort: CommandPort
) : ExecContainerService {
    private val containerInputStreams = ConcurrentHashMap<String, OutputStream>()

    override fun execCmd(application: Application, cmd: String): List<String> {
        val result = mutableListOf<String>()
        containerPort.execute {
            executeCmd(application, "/", cmd) { output ->
                result.add(output)
            }
        }
        return result
    }

    override fun execCmd(application: Application, session: WebSocketSession, cmd: String) {
        containerInputStreams[session.id]?.let {
            it.write("${cmd}\n".toByteArray())
            it.flush()
        } ?: throw ContainerNotConnectedException()
    }

    override fun initContainerTty(application: Application, session: WebSocketSession) =
        containerPort.execute {
            attachContainer(application) { response ->
                if (session.isOpen) {
                    session.sendMessage(TextMessage(response))
                }
            }
        }?.let {
            containerInputStreams[session.id] = it
        } ?: throw ContainerNotConnectedException()
}