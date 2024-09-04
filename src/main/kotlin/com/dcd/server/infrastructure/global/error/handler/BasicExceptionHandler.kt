package com.dcd.server.infrastructure.global.error.handler

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BasicExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @ExceptionHandler(BasicException::class)
    fun handleBasicException(request: HttpServletRequest, ex: BasicException): ResponseEntity<ErrorResponse> {
        log.error(request.method)
        log.error(request.requestURI)
        val errorCode = ex.errorCode
        log.error(errorCode.msg)
        log.error("${errorCode.code}")
        return ResponseEntity(
            ErrorResponse(errorCode),
            HttpStatus.valueOf(errorCode.code)
        )
    }
}