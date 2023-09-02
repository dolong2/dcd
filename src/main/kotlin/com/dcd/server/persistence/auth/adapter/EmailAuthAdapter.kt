package com.dcd.server.persistence.auth.adapter

import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.persistence.auth.entity.EmailAuthEntity

fun EmailAuth.toEntity(): EmailAuthEntity =
    EmailAuthEntity(
        email = this.email,
        code = this.code
    )

fun EmailAuthEntity.toDomain(): EmailAuth =
    EmailAuth(
        email = this.email,
        code = this.code
    )