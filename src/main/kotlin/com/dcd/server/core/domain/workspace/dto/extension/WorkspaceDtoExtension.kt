package com.dcd.server.core.domain.workspace.dto.extension

import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.model.Workspace

fun Workspace.toDto(applicationList: List<Application>): WorkspaceResDto =
    WorkspaceResDto(
        id = this.id,
        title = this.title,
        description = this.description,
        owner = this.owner.toDto(),
        applicationList = applicationList.map { it.toDto() }
    )