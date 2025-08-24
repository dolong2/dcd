package com.dcd.server.persistence.auth.adapter

import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.persistence.auth.entity.EmailAuthEntity

fun EmailAuth.toEntity(): EmailAuthEntity =
    EmailAuthEntity(
        email = this.email,
        code = this.code,
        certificate = this.certificate,
        usage = this.usage
    )

fun EmailAuthEntity.toDomain(): EmailAuth =
    EmailAuth(
        email = this.email,
        code = this.code,
        certificate = this.certificate,
        usage = this.usage
    )