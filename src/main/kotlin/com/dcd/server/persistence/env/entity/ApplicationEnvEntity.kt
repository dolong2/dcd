package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.env.entity.common.Env
import jakarta.persistence.*
import java.util.*
import kotlin.collections.List

@Entity
class ApplicationEnvEntity(
    id: UUID = UUID.randomUUID(),
    name: String,
    description: String,
    @OneToMany(mappedBy = "applicationEnv", cascade = [CascadeType.REMOVE])
    val details: List<ApplicationEnvDetailEntity>
) : Env(id, name, description)