package com.dcd.server.persistence.user.adapter

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.persistence.user.entity.UserJpaEntity
import java.util.*

fun User.toEntity(): UserJpaEntity =
    UserJpaEntity(
        id = UUID.fromString(this.id),
        email = this.email,
        name = this.name,
        password = this.password,
        roles = this.roles,
        status = this.status
    )

fun UserJpaEntity.toDomain(): User =
    User(
        id = this.id.toString(),
        email = this.email,
        name = this.name,
        password = this.password,
        roles = this.roles,
        status = this.status
    )