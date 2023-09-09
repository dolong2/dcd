package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.user.model.User

interface QueryApplicationPort {
    fun findAllByUser(user: User): List<Application>
    fun findById(id: String): Application?
}