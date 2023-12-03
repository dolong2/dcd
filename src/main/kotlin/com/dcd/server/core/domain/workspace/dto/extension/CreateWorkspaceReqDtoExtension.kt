package com.dcd.server.core.domain.workspace.dto.extension

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.model.Workspace

fun CreateWorkspaceReqDto.toEntity(user: User): Workspace =
    Workspace(
        title = this.title,
        description = this.description,
        owner = user
    )