package com.dcd.server.persistence.application.entity

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.*


@Entity
class ApplicationJpaEntity(
    @Id
    val id: String,
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String,
    @ElementCollection
    @CollectionTable(name = "application_env_table",
        joinColumns = [JoinColumn(name = "application_id", referencedColumnName = "id")])
    @MapKeyColumn(name = "env_key")
    @Column(name = "env_value")
    val env: Map<String, String>,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)