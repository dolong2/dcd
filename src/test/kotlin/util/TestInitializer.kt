package util

import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.application.repository.ApplicationRepository
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.user.repository.UserRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import com.dcd.server.persistence.workspace.repository.WorkspaceRepository
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.AfterEach
import io.kotest.core.spec.BeforeEach
import io.kotest.extensions.spring.SpringExtension
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

@Component
@Profile("test")
class TestInitializer(
    private val userRepository: UserRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val applicationRepository: ApplicationRepository
) : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    val startTest: BeforeEach = {
        val applicationOwner = UserGenerator.generateUser(email = "ownerEmail", name = "applicationOwner")
        val testUser = UserGenerator.generateUser()

        val workspace = WorkspaceGenerator.generateWorkspace(user = applicationOwner)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)

        userRepository.save(applicationOwner.toEntity())
        userRepository.save(testUser.toEntity())
        workspaceRepository.save(workspace.toEntity())
        applicationRepository.save(application.toEntity())
    }

    val afterTest: AfterEach = {
        applicationRepository.deleteAll()
        workspaceRepository.deleteAll()
        userRepository.deleteAll()
    }
}