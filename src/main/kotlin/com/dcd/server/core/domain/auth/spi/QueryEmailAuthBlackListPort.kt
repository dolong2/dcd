package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

interface QueryEmailAuthBlackListPort {
    fun isBlocked(email: String, usage: EmailAuthUsage): Boolean
}
