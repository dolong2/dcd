package com.dcd.server.persistence.env.entity.common

import jakarta.persistence.*
import java.util.UUID

@MappedSuperclass
open class Env(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    val name: String,
    val description: String,
)