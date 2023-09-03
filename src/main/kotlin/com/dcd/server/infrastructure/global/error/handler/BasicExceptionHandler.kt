package com.dcd.server.infrastructure.global.error.handler

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.infrastructure.global.error.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BasicExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @ExceptionHandler(BasicException::class)
    fun handleBasicException(request: HttpServletRequest, ex: BasicException): ErrorResponse {
        log.error(request.method)
        log.error(request.requestURI)
        val errorCode = ex.errorCode
        log.error(errorCode.msg)
        log.error("${errorCode.code}")
        return ErrorResponse(errorCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(request: HttpServletRequest, ex: MethodArgumentNotValidException): ErrorResponse {
        log.error(request.method)
        log.error(request.requestURI)
        val errorCode = ErrorCode.BAD_REQUEST
        log.error(errorCode.msg)
        log.error("${errorCode.code}")
        return ErrorResponse(errorCode)
    }
}