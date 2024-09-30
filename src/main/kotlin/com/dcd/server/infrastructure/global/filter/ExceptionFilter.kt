package com.dcd.server.infrastructure.global.filter

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import com.dcd.server.presentation.domain.application.exception.InvalidConnectionInfoException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.socket.server.HandshakeFailureException

class ExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            when (ex) {
                is BasicException -> {
                    logErrorResponse(ex.errorCode, ex)
                    writeErrorResponse(response, ex)
                }
                is ServletException -> {
                    val errorCode = ErrorCode.BAD_REQUEST
                    logErrorResponse(errorCode, ex)
                    writeErrorResponse(response, BasicException(errorCode))
                }
                else -> {
                    ex.printStackTrace()
                    log.error(ex.message)
                    val errorCode = ErrorCode.INTERNAL_ERROR
                    logErrorResponse(errorCode, ex)
                    writeErrorResponse(response, BasicException(errorCode))
                }
            }
        }
    }

    private fun logErrorResponse(errorCode: ErrorCode, ex: Exception) {
        log.error(errorCode.msg)
        log.error(ex.message)
    }

    private fun writeErrorResponse(response: HttpServletResponse, exception: BasicException) {
        val errorCode = exception.errorCode
        val responseBody = objectMapper.writeValueAsString(ErrorResponse(errorCode))
        response.status = errorCode.code
        response.characterEncoding = Charsets.UTF_8.name()
        response.contentType = "application/json"
        response.writer.write(responseBody)
    }
}