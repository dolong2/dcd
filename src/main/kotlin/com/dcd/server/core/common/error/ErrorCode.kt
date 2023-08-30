package com.dcd.server.core.common.error

enum class ErrorCode(
    val msg: String,
    val code: Int
) {
    BAD_REQUEST("요청이 옳바르지 않음", 400),

    UNAUTHORIZED("권한이 없음", 401),
    EXPIRED_TOKEN("토큰이 만료됨", 401),

    FORBIDDEN("금지된 요청", 403),
    NOT_VALID_TOKEN("토큰이 유효하지 않음", 403),

    NOT_FOUND("해당 리소스를 찾을 수 없음", 404),

    INTERNAL_ERROR("서버 내부 에러", 500)
}