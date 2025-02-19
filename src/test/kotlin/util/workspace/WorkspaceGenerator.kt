package util.workspace

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import util.user.UserGenerator
import java.util.*

object WorkspaceGenerator {
    fun generateWorkspace(
        id: String = UUID.randomUUID().toString(),
        title: String = "testTitle",
        description: String = "testDescription",
        user: User = UserGenerator.generateUser(),
        globalEnv: Map<String, String> = mapOf()
    ): Workspace =
        Workspace(
            id = id,
            title = title,
            description = description,
            owner = user,
            globalEnv = globalEnv
        )
}