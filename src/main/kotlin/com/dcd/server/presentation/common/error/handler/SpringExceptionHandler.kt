package com.dcd.server.presentation.common.error.handler

import com.dcd.server.presentation.common.error.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.UUID

/**
 * Spring MVC 표준 예외(요청 바디 파싱 실패, 필수 파라미터 누락, 미지원 메서드/미디어 타입 등)를
 * Spring이 판단한 status 그대로 ErrorResponse 스키마로 응답
 */
@RestControllerAdvice
class SpringExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        // 경로상의 식별자가 UUID 형식이 아니면 존재하지 않는 리소스로 취급
        if (ex is MethodArgumentTypeMismatchException
            && ex.requiredType == UUID::class.java
            && ex.parameter.hasParameterAnnotation(PathVariable::class.java)
        ) {
            return handleExceptionInternal(ex, null, headers, HttpStatus.NOT_FOUND, request)
        }
        return super.handleTypeMismatch(ex, headers, status, request)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        if (request is ServletWebRequest) {
            if (request.response?.isCommitted == true)
                return null
            log.error(request.request.method)
            log.error(request.request.requestURI)
        }
        val errorResponse = ErrorResponse.of(statusCode)
        log.error(errorResponse.message)
        log.error(ex.message)
        return ResponseEntity(errorResponse, headers, statusCode)
    }
}
