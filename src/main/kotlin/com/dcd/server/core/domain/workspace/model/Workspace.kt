package com.dcd.server.core.domain.workspace.model

import com.dcd.server.core.domain.user.model.User
import java.util.*

data class Workspace(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val globalEnv: Map<String, String> = mapOf(),
    val owner: User
) {
    val networkName: String = title.replace(" ", "_")

    override fun equals(other: Any?): Boolean {
        if (other !is Workspace) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}