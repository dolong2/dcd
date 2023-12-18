package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace

interface ValidateWorkspaceOwnerService {
    fun validateOwner(user: User, workspace: Workspace)

    fun validateOwner(workspace: Workspace)
}