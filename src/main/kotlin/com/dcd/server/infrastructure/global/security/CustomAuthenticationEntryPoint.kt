package com.dcd.server.infrastructure.global.security

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
): AuthenticationEntryPoint {

    private val log = LoggerFactory.getLogger(this.javaClass.simpleName)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.error("==========Access Denied==========")
        val errorCode = ErrorCode.FORBIDDEN
        val responseString = objectMapper.writeValueAsString(ErrorResponse(errorCode.code, errorCode.msg))
        response.characterEncoding = "UTF-8"
        response.status = errorCode.code
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(responseString)
    }
}