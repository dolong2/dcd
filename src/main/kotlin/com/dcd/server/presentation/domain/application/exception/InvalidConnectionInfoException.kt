package com.dcd.server.presentation.domain.application.exception

import org.springframework.web.socket.CloseStatus

class InvalidConnectionInfoException(
    message: String,
    val closeStatus: CloseStatus,
) : RuntimeException(message)
