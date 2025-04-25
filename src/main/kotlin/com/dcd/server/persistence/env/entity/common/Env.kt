package com.dcd.server.persistence.env.entity.common

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

@MappedSuperclass
open class Env(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    @Column(name = "env_key")
    val key: String,
    @Column(name = "env_value")
    val value: String,
    val encryption: Boolean
)