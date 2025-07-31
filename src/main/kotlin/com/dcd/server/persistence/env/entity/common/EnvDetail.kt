package com.dcd.server.persistence.env.entity.common

import jakarta.persistence.Embeddable

@Embeddable
open class EnvDetail(
    open val key: String,
    open val value: String,
    open val encryption: Boolean = false
)