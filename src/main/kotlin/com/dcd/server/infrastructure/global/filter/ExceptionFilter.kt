package com.dcd.server.infrastructure.global.filter

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

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
                    logErrorResponse(request, ex.errorCode)
                    writeErrorResponse(response, ex)
                }
                else -> {
                    ex.printStackTrace()
                    val errorCode = ErrorCode.INTERNAL_ERROR
                    logErrorResponse(request, errorCode)
                    writeErrorResponse(response, BasicException(errorCode))
                }
            }
        }
    }

    private fun logErrorResponse(request: HttpServletRequest, errorCode: ErrorCode, ex: Exception) {
        log.error(request.method)
        log.error(request.requestURI)
        log.error(errorCode.msg)
        log.error(ex.message)
        log.error("${errorCode.code}")
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