package util.workspace

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import util.user.UserGenerator
import java.util.*

object WorkspaceGenerator {
    fun createWorkspace(
        id: String = UUID.randomUUID().toString(),
        title: String = "testTitle",
        description: String = "testDescription",
        user: User = UserGenerator.createUser()
    ): Workspace =
        Workspace(
            id = id,
            title = title,
            description = description,
            owner = user
        )
}