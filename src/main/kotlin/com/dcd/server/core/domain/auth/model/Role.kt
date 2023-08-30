package com.dcd.server.core.domain.auth.model

enum class Role(private val description: String) {
    ROLE_ADMIN("관리자"),
    ROLE_DEVELOPER("개발자"),
    ROLE_USER("사용자")
}