package com.dcd.server.persistence.auth.repository

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.persistence.auth.entity.EmailAuthBlackListEntity
import org.springframework.data.repository.CrudRepository

interface EmailAuthBlackListRepository : CrudRepository<EmailAuthBlackListEntity, String> {
    fun existsByEmailAndUsage(email: String, usage: EmailAuthUsage): Boolean
}
