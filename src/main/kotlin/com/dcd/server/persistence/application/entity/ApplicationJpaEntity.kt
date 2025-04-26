package com.dcd.server.persistence.application.entity

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.UUID


@Entity
@Table(name = "application_entity")
class ApplicationJpaEntity(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    val name: String,
    val description: String?,
    @Enumerated(EnumType.STRING)
    val applicationType: ApplicationType,
    val githubUrl: String?,
    @OneToMany(mappedBy = "application", cascade = [CascadeType.REMOVE])
    val env: List<ApplicationEnvEntity>,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
    val port: Int,
    val externalPort: Int,
    val version: String,
    @Enumerated(EnumType.STRING)
    val status: ApplicationStatus,
    val failureReason: String?,
    @ElementCollection
    @CollectionTable(name = "application_label_entity", joinColumns = [JoinColumn(name = "application_id")])
    @Column(name = "label")
    val labels: List<String>
)