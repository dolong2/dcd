package com.dcd.server.core.domain.env.model

import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.*

data class ApplicationEnv(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val details: List<ApplicationEnvDetail>,
    val workspace: Workspace,
    val labels: List<String>
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ApplicationEnv) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}