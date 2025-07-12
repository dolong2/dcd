package com.dcd.server.persistence.env.entity.common

import jakarta.persistence.*
import java.util.UUID

@MappedSuperclass
open class Env(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    @Column(name = "env_key")
    val key: String,
    @Column(name = "env_value")
    val value: String,
    val encryption: Boolean
)