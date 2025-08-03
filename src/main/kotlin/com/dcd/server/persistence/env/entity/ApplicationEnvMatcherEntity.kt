package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
class ApplicationEnvMatcherEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "application_id")
    val application: ApplicationJpaEntity,
    @ManyToOne
    @JoinColumn(name = "env_id")
    val applicationEnv: ApplicationEnvEntity
) {
}