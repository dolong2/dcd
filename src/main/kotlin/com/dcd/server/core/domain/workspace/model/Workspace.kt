package com.dcd.server.core.domain.workspace.model

import com.dcd.server.core.domain.user.model.User
import java.util.*

data class Workspace(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val globalEnv: Map<String, String> = mapOf(),
    val owner: User
)