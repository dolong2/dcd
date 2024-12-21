package com.dcd.server.core.domain.user.spi

import com.dcd.server.core.domain.user.model.User

interface CommandUserPort {
    fun save(user: User)

    fun delete(user: User)
}