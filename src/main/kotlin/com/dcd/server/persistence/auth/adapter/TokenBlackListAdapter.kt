package com.dcd.server.persistence.auth.adapter

import com.dcd.server.core.domain.auth.model.TokenBlackList
import com.dcd.server.persistence.auth.entity.TokenBlackListEntity

fun TokenBlackList.toEntity(): TokenBlackListEntity =
    TokenBlackListEntity(
        token = this.token,
        ttl = this.ttl
    )

fun TokenBlackListEntity.toDomain(): TokenBlackList =
    TokenBlackList(
        token = this.token,
        ttl = this.ttl
    )