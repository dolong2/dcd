package com.dcd.server.infrastructure.test

import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator

@Component
@Profile("test")
class TestConfig(
    private val commandUserPort: CommandUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandApplicationPort: CommandApplicationPort,
    passwordEncoder: PasswordEncoder
) {

    private val applicationOwner = UserGenerator.generateUser(id = "user1", email = "ownerEmail", name = "applicationOwner", password = passwordEncoder.encode("testPassword"))
    private val testUser = UserGenerator.generateUser(id = "user2", password = passwordEncoder.encode("testPassword"))
    private val workspace = WorkspaceGenerator.generateWorkspace(user = applicationOwner)
    private val application = ApplicationGenerator.generateApplication(workspace = workspace)

    @PostConstruct
    fun beforeSpec() {
        commandUserPort.save(applicationOwner)
        commandUserPort.save(testUser)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
    }

    @PreDestroy
    fun afterSpec() {
        commandApplicationPort.delete(application)
        commandWorkspacePort.delete(workspace)
        commandUserPort.delete(testUser)
        commandUserPort.delete(applicationOwner)
    }
}