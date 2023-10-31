package com.dcd.server.core.domain.workspace.model

import com.dcd.server.core.domain.user.model.User

data class Workspace(
    val title: String,
    val description: String,
    val owner: User
)