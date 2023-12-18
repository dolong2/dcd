package com.dcd.server.core.domain.workspace.service.impl

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import org.springframework.stereotype.Service

@Service
class ValidateWorkspaceOwnerServiceImpl : ValidateWorkspaceOwnerService{
    override fun validateOwner(user: User, workspace: Workspace) {
        if (user.equals(workspace.owner).not())
            throw WorkspaceOwnerNotSameException()
    }
}