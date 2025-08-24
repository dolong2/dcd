package util.domain

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.workspace.model.Workspace
import util.workspace.WorkspaceGenerator
import java.util.*

object DomainGenerator {
    fun generateDomain(
        id: String = UUID.randomUUID().toString(),
        name: String = "test",
        description: String = "test",
        workspace: Workspace = WorkspaceGenerator.generateWorkspace(),
        application: Application? = null
    ): Domain =
        Domain(
            id = id,
            name = name,
            description = description,
            workspace = workspace,
            application = application
        )
}