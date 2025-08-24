package com.dcd.server.persistence.auth.adapter

import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.persistence.auth.entity.RefreshTokenEntity

fun RefreshToken.toEntity(): RefreshTokenEntity =
    RefreshTokenEntity(
        userId = this.userId,
        token = this.token,
        refreshTTL = this.refreshTTL
    )

fun RefreshTokenEntity.toDomain(): RefreshToken =
    RefreshToken(
        userId = this.userId,
        token = this.token,
        refreshTTL = this.refreshTTL
    )