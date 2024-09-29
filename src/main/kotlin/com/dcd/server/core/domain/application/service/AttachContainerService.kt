package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application
import org.springframework.web.socket.WebSocketSession

interface AttachContainerService {
    fun attachService(application: Application, session: WebSocketSession, cmd: String)
}