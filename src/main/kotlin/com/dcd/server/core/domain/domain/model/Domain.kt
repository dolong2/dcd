package com.dcd.server.core.domain.domain.model

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.workspace.model.Workspace

data class Domain(
    val id: String,
    val name: String,
    val description: String,
    val application: Application?,
    val workspace: Workspace,
) {
    fun getDomainName(): String =
        "${name}.dolong2.co.kr"
}
