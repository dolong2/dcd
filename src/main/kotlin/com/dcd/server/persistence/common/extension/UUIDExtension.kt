package com.dcd.server.persistence.common.extension

import java.util.UUID

/**
 * 외부에서 전달된 식별자가 UUID 형식이 아니면 null을 반환
 * - 형식이 올바르지 않은 식별자는 존재하지 않는 리소스로 취급하기 위해 사용
 */
fun String.toUUIDOrNull(): UUID? =
    runCatching { UUID.fromString(this) }.getOrNull()
