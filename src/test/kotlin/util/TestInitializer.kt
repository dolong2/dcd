package util

import com.dcd.server.ServerApplication
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.application.repository.ApplicationRepository
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.user.repository.UserRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import com.dcd.server.persistence.workspace.repository.WorkspaceRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.ActiveProfiles
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

@ActiveProfiles("test")
@TestConfiguration
@SpringBootTest(classes = [ServerApplication::class])
class TestInitializer(
    userRepository: UserRepository,
    workspaceRepository: WorkspaceRepository,
    applicationRepository: ApplicationRepository
) {
    init {
        val applicationOwner = UserGenerator.generateUser(email = "ownerEmail", name = "applicationOwner")
        val testUser = UserGenerator.generateUser()

        val workspace = WorkspaceGenerator.generateWorkspace(user = applicationOwner)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)

        userRepository.save(applicationOwner.toEntity())
        userRepository.save(testUser.toEntity())
        workspaceRepository.save(workspace.toEntity())
        applicationRepository.save(application.toEntity())
    }
}