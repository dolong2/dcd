package com.dcd.server.core.common.error

enum class ErrorCode(
    val msg: String,
    val code: Int
) {
    BAD_REQUEST("요청이 옳바르지 않음", 400),
    INVALID_ROLE("유효하지않은 권한", 400),
    PASSWORD_NOT_CORRECT("패스워드가 옳바르지 않음", 400),
    ALREADY_USER_EXIST("이미 해당 유저가 존재함", 400),
    APPLICATION_OPTION_NOT_VALID("애플리케이션 옵션이 유효하지 않음", 400),

    UNAUTHORIZED("권한이 없음", 401),
    EXPIRED_TOKEN("토큰이 만료됨", 401),
    EXPIRED_REFRESH_TOKEN("리프레시 토큰이 만료됨", 401),
    EXPIRED_AUTH_CODE("인증코드가 만료됨", 401),

    FORBIDDEN("금지된 요청", 403),
    NOT_VALID_TOKEN("토큰이 유효하지 않음", 403),
    NOT_VALID_CODE("코드가 유효하지 않음", 403),
    NOT_CERTIFICATE_MAIL("메일인증후 진행해주세요", 403),
    NOT_SAME_APPLICATION_OWNER("애플리케이션 소유자가 옳바르지 않음", 403),

    NOT_FOUND("해당 리소스를 찾을 수 없음", 404),
    USER_NOT_FOUND("해당 유저를 찾을 수 없음", 404),
    APPLICATION_NOT_FOUND("해당 애플리케이션을 찾을 수 없음", 404),

    INTERNAL_ERROR("서버 내부 에러", 500)
}