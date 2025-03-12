package com.dcd.server.presentation.common.error.handler

import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.presentation.common.error.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SpringExceptionHandler{
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(request: HttpServletRequest, ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        log.error(request.method)
        log.error(request.requestURI)
        val errorCode = ErrorCode.BAD_REQUEST
        log.error(errorCode.msg)
        return ResponseEntity(
            ErrorResponse(errorCode),
            HttpStatusCode.valueOf(errorCode.code)
        )
    }
}