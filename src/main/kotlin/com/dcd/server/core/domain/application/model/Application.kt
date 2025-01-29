package com.dcd.server.core.domain.application.model

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.*

data class Application(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val applicationType: ApplicationType,
    val githubUrl: String?,
    val env: Map<String, String>,
    val version: String,
    val workspace: Workspace,
    val port: Int,
    val externalPort: Int,
    val status: ApplicationStatus,
    val labels: List<String>
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Application) return false
        return this.id == other.id
    }
}