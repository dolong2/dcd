package com.dcd.server.core.common.spi

import com.dcd.server.core.domain.user.model.User

interface CompareUserPort {
    fun compareTwoUserEntity(user: User, another: User): Boolean
}