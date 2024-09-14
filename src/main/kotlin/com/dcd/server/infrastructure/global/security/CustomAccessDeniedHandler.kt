package com.dcd.server.infrastructure.global.security

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        log.error(request.method)
        log.error(request.requestURI)
        val errorCode = ErrorCode.INVALID_ROLE
        log.error(errorCode.msg)
        val result = objectMapper.writeValueAsString(ErrorResponse(errorCode))
        response.characterEncoding = Charsets.UTF_8.name()
        response.status = errorCode.code
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(result)
    }
}