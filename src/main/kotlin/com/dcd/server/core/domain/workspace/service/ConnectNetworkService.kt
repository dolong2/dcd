package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.domain.workspace.model.Workspace

interface ConnectNetworkService {
    fun connectNetworkByWorkspace(workspace: Workspace)
}