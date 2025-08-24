package com.dcd.server.core.domain.user.dto.extension

import com.dcd.server.core.domain.user.dto.response.UserProfileResDto
import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceProfileResDto

fun User.toDto(): UserResDto =
    UserResDto(
        id =  this.id,
        email = this.email,
        name = this.name,
        status = this.status
    )

fun UserResDto.toProfileDto(workspaceList: List<WorkspaceProfileResDto>): UserProfileResDto =
    UserProfileResDto(
        user = this,
        workspaces = workspaceList
    )