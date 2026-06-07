package com.dcd.server.core.domain.auth.model.enums

enum class EmailAuthUsage(val failThreshold: Int) {
    SIGNUP(10),
    CHANGE_PASSWORD(5)
}
