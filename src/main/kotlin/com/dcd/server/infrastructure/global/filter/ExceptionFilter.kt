package com.dcd.server.infrastructure.global.filter

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
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
                    writeErrorResponse(response, ex)
                }
                else -> {
                    ex.printStackTrace()
                    writeErrorResponse(response, BasicException(ErrorCode.INTERNAL_ERROR))
                }
            }
        }
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