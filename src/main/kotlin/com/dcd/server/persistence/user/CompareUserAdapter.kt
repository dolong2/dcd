package com.dcd.server.persistence.user

import com.dcd.server.core.common.spi.CompareUserPort
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.persistence.user.adapter.toEntity
import org.springframework.stereotype.Component

@Component
class CompareUserAdapter : CompareUserPort{
    override fun compareTwoUserEntity(user: User, another: User): Boolean =
        user.toEntity() == another.toEntity()
}