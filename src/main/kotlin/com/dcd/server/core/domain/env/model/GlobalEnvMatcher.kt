package com.dcd.server.core.domain.env.model

import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.*

class GlobalEnvMatcher(
    val id: UUID = UUID.randomUUID(),
    val workspace: Workspace,
    val globalEnv: GlobalEnv
)