package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.EmailAuth

interface QueryEmailAuthPort {
    fun findByEmail(email: String): List<EmailAuth>
    fun findByCode(code: String): EmailAuth?
    fun existsByCodeAndEmail(email: String, code: String): Boolean
    fun existsByCode(code: String): Boolean
}