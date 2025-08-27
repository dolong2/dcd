package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.env.entity.common.Env
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.*
import java.util.*
import kotlin.collections.List

@Entity
class ApplicationEnvEntity(
    id: UUID = UUID.randomUUID(),
    name: String,
    description: String,
    @OneToMany(mappedBy = "applicationEnv", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    val details: List<ApplicationEnvDetailEntity>,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
    @ElementCollection
    @CollectionTable(name = "application_env_label_entity", joinColumns = [JoinColumn(name = "application_env_id")])
    @Column(name = "label")
    val labels: List<String>
) : Env(id, name, description)