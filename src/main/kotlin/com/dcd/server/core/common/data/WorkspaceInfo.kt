package com.dcd.server.core.common.data

import com.dcd.server.core.domain.workspace.model.Workspace
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
data class WorkspaceInfo(
    var workspace: Workspace? = null
)
