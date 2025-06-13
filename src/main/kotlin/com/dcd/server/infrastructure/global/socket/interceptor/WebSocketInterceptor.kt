package com.dcd.server.infrastructure.global.socket.interceptor

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.infrastructure.global.jwt.exception.TokenNotValidException
import com.dcd.server.presentation.domain.application.exception.InvalidConnectionInfoException
import org.springframework.http.HttpStatusCode
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

class WebSocketInterceptor(
    private val parseTokenAdapter: ParseTokenAdapter
) : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        request.headers["Authorization"]?.first()
            ?.run { attributes["accessToken"] = parseTokenAdapter.parseToken(this) ?: throw TokenNotValidException() }
        val query = request.uri.query
        val applicationId = query?.split("=")?.get(1)
            ?: throw InvalidConnectionInfoException("접속할 애플리케이션 아이디가 주어지지 않음", CloseStatus.PROTOCOL_ERROR)

        attributes["applicationId"] = applicationId

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {
        when (exception) {
            is InvalidConnectionInfoException -> {
                response.setStatusCode(HttpStatusCode.valueOf(400))
            }

            is BasicException -> {
                val errorCode = exception.errorCode
                response.setStatusCode(HttpStatusCode.valueOf(errorCode.code))
            }

            is Exception -> {
                response.setStatusCode(HttpStatusCode.valueOf(500))
            }
        }
    }
}