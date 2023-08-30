package com.dcd.server.core.domain.user.spi

import com.dcd.server.core.domain.user.model.User

interface QueryUserPort {
    fun findById(id: String): User?
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}