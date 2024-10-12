package com.dcd.server.persistence.application.entity

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.persistence.user.entity.UserJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.*


@Entity
@Table(name = "application")
class ApplicationJpaEntity(
    @Id
    val id: String,
    val name: String,
    val description: String?,
    @Enumerated(EnumType.STRING)
    val applicationType: ApplicationType,
    val githubUrl: String?,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "application_env_table",
        joinColumns = [JoinColumn(name = "application_id", referencedColumnName = "id")])
    @MapKeyColumn(name = "env_key")
    @Column(name = "env_value")
    val env: Map<String, String>,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
    val port: Int,
    val externalPort: Int,
    val version: String,
    @Enumerated(EnumType.STRING)
    val status: ApplicationStatus,
    @ElementCollection
    @CollectionTable(name = "application_labels", joinColumns = [JoinColumn(name = "application_id")])
    @Column(name = "label")
    val labels: List<String>
)