package com.dcd.server.persistence.common.extension

import java.util.UUID

private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

/**
 * 외부에서 전달된 식별자가 UUID 형식이 아니면 null을 반환
 * - 형식이 올바르지 않은 식별자는 존재하지 않는 리소스로 취급하기 위해 사용
 * - UUID.fromString은 "1-2-3-4-5"와 같은 비표준 형식도 허용하므로 정규식으로 먼저 검증
 */
fun String.toUUIDOrNull(): UUID? {
    if (!UUID_REGEX.matches(this)) return null
    return runCatching { UUID.fromString(this) }.getOrNull()
}
