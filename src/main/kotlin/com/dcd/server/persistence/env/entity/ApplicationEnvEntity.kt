package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.env.entity.common.Env
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
class ApplicationEnvEntity(
    id: UUID = UUID.randomUUID(),
    key: String,
    value: String,
    encryption: Boolean,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    val application: ApplicationJpaEntity? = null
) : Env(id, key, value, encryption)