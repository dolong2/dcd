package com.dcd.server.infrastructure.global.socket.interceptor

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

@Component
class WebSocketInterceptor(
    private val parseTokenAdapter: ParseTokenAdapter,
    private val objectMapper: ObjectMapper
) : HandshakeInterceptor {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    /**
     * beforeHandshake에서 예외를 던지면 HandshakeFailureException으로 감싸져 500으로 응답되므로,
     * 예외 대신 원인에 맞는 status를 응답에 기록하고 false를 반환해 핸드셰이크를 중단
     */
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        val authorization = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authorization != null) {
            attributes["accessToken"] = parseTokenAdapter.parseToken(authorization)
                ?: return reject(response, ErrorCode.NOT_VALID_TOKEN)
        }

        val applicationId = request.uri.query
            ?.split("=")
            ?.getOrNull(1)
            ?.takeIf { it.isNotBlank() }
            ?: return reject(response, ErrorCode.BAD_REQUEST, "접속할 애플리케이션 아이디가 주어지지 않음")

        attributes["applicationId"] = applicationId

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {}

    private fun reject(response: ServerHttpResponse, errorCode: ErrorCode, message: String = errorCode.msg): Boolean {
        log.error("${errorCode.code}")
        log.error(message)

        val responseBody = objectMapper.writeValueAsBytes(mapOf("status" to errorCode.code, "message" to message))
        response.setStatusCode(HttpStatusCode.valueOf(errorCode.code))
        response.headers.contentType = MediaType.APPLICATION_JSON
        response.body.write(responseBody)
        return false
    }
}
