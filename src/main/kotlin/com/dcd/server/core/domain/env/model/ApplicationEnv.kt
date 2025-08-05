package com.dcd.server.core.domain.env.model

import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.*

data class ApplicationEnv(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val details: List<ApplicationEnvDetail>,
    val workspace: Workspace
)