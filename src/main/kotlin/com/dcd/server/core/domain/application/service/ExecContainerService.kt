package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application
import org.springframework.web.socket.WebSocketSession
import java.io.OutputStream

interface ExecContainerService {
    fun execCmd(application: Application, cmd: String): List<String>
    fun execCmd(application: Application, session: WebSocketSession, cmd: String)
    fun initContainerTty(application: Application, session: WebSocketSession)
}