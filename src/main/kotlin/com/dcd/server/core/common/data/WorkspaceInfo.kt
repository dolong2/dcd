package com.dcd.server.core.common.data

import com.dcd.server.core.domain.workspace.model.Workspace
import org.springframework.stereotype.Component

@Component
data class WorkspaceInfo(
    var workspace: Workspace? = null
)
