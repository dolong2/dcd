package com.dcd.server.presentation.common.error.handler

import com.dcd.server.presentation.common.error.response.ErrorResponse
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 예외 핸들러에서 처리되지 않고 sendError로 /error 포워딩된 요청을
 * Spring Boot 기본 응답 스키마 대신 원래 status를 유지한 ErrorResponse로 응답
 */
@RestController
class CustomErrorController : ErrorController {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @RequestMapping("\${server.error.path:\${error.path:/error}}")
    fun handleError(request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as? Int ?: 500
        val statusCode = HttpStatusCode.valueOf(status)
        val errorResponse = ErrorResponse.of(statusCode)
        log.error(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)?.toString())
        log.error(errorResponse.message)
        return ResponseEntity(errorResponse, statusCode)
    }
}
