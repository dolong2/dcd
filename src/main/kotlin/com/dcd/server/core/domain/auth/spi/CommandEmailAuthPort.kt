package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.EmailAuth

interface CommandEmailAuthPort {
    fun save(emailAuth: EmailAuth)
    fun deleteByCode(code: String)
    fun deleteByEmailAndCode(email: String, code: String)
}