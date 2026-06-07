package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

interface QueryEmailAuthPort {
    fun findByEmail(email: String): List<EmailAuth>
    fun findByCode(code: String): EmailAuth?
    fun findByEmailAndUsage(email: String, usage: EmailAuthUsage): EmailAuth?
    fun existsByCodeAndEmail(email: String, code: String): Boolean
    fun existsByEmail(email: String): Boolean
}