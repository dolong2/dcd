package com.dcd.server.persistence.auth.repository

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.persistence.auth.entity.EmailAuthEntity
import org.springframework.data.repository.CrudRepository

interface EmailAuthRepository : CrudRepository<EmailAuthEntity, String> {
    fun deleteByEmailAndCode(email: String, code: String)
    fun findByEmail(email: String): List<EmailAuthEntity>
    fun findByEmailAndUsage(email: String, usage: EmailAuthUsage): EmailAuthEntity?
    fun existsByEmailAndCode(email: String, code: String): Boolean
    fun existsByEmail(email: String): Boolean
}