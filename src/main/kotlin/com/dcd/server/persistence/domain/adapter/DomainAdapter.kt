package com.dcd.server.persistence.domain.adapter

import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.persistence.application.adapter.toDomain
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.domain.entity.DomainJpaEntity
import com.dcd.server.persistence.user.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.workspace.adapter.toDomain
import com.dcd.server.persistence.workspace.adapter.toEntity
import java.util.UUID

fun Domain.toEntity(): DomainJpaEntity =
    DomainJpaEntity(
        id = UUID.fromString(this.id),
        name = this.name,
        description = this.description,
        application = this.application?.toEntity(),
        workspace = this.workspace.toEntity()
    )

fun DomainJpaEntity.toDomain(): Domain =
    Domain(
        id = this.id.toString(),
        name = this.name,
        description = this.description,
        application = this.application?.toDomain(),
        workspace = this.workspace.toDomain()
    )