package com.dcd.server.presentation.common.error.response

import com.dcd.server.core.common.error.ErrorCode
import org.springframework.http.HttpStatusCode

data class ErrorResponse(
    val status: Int,
    val message: String
) {
    constructor(errorCode: ErrorCode) : this(errorCode.code, errorCode.msg)

    companion object {
        /**
         * 도메인 예외가 아닌 프레임워크 수준 에러의 status를 그대로 유지하고, 메시지만 status에 대응되는 ErrorCode에서 가져옴
         */
        fun of(statusCode: HttpStatusCode): ErrorResponse {
            val errorCode = when (statusCode.value()) {
                400 -> ErrorCode.BAD_REQUEST
                401 -> ErrorCode.UNAUTHORIZED
                403 -> ErrorCode.FORBIDDEN
                404 -> ErrorCode.NOT_FOUND
                405 -> ErrorCode.METHOD_NOT_ALLOWED
                406 -> ErrorCode.NOT_ACCEPTABLE
                409 -> ErrorCode.CONFLICT
                415 -> ErrorCode.UNSUPPORTED_MEDIA_TYPE
                429 -> ErrorCode.TOO_MANY_REQUESTS
                else -> if (statusCode.is4xxClientError) ErrorCode.BAD_REQUEST else ErrorCode.INTERNAL_ERROR
            }
            return ErrorResponse(statusCode.value(), errorCode.msg)
        }
    }
}
