package com.dcd.server.persistence.application.entity

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne


@Entity
class ApplicationJpaEntity(
    @Id
    val id: String,
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)