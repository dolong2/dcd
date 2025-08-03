package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.env.entity.common.EnvDetail
import jakarta.persistence.*
import java.util.*

@Entity
class GlobalEnvDetailEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val envDetail: EnvDetail,
    @ManyToOne
    @JoinColumn(name = "env_id")
    val globalEnv: GlobalEnvEntity,
) {
}