package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

interface CommandEmailAuthBlackListPort {
    fun addToBlackList(email: String, usage: EmailAuthUsage)
}
