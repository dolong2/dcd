package com.dcd.server.core.domain.application.model

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.*

data class Application(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String?,
    val env: List<ApplicationEnv>,
    val version: String,
    val workspace: Workspace,
    val port: Int,
    val externalPort: Int,
    val status: ApplicationStatus,
    val failureReason: String? = null,
    val labels: List<String>
) {
    val containerName = "${name.replace(" ", "_").lowercase()}-$id"

    override fun equals(other: Any?): Boolean {
        if (other !is Application) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}