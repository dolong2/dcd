package util.application

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.workspace.model.Workspace
import util.workspace.WorkspaceGenerator

object ApplicationGenerator {
    fun generateApplication(
        id: String = "testId",
        name: String = "testName",
        description: String = "testDescription",
        applicationType: ApplicationType = ApplicationType.SPRING_BOOT,
        env: Map<String, String> = mapOf(),
        githubUrl: String = "testUrl",
        version: String = "17",
        workspace: Workspace = WorkspaceGenerator.generateWorkspace(),
        port: Int = 8080,
        status: ApplicationStatus = ApplicationStatus.STOPPED,
        labels: List<String> = listOf()
    ): Application =
        Application(
            id = id,
            name = name,
            description = description,
            applicationType = applicationType,
            env = env,
            githubUrl = githubUrl,
            version = version,
            workspace = workspace,
            port = port,
            externalPort = port,
            status = status,
            labels = labels
        )
}