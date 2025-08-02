package com.dcd.server.persistence.env.entity.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
open class EnvDetail(
    @Column(name = "env_key")
    open val key: String,
    @Column(name = "env_value")
    open val value: String,
    open val encryption: Boolean = false
)