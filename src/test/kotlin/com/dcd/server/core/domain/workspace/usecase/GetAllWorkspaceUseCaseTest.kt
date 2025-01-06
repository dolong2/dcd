package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetAllWorkspaceUseCaseTest(
    private val getAllWorkspaceUseCase: GetAllWorkspaceUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort
) : BehaviorSpec({
    val userId = "user2"
    val firstWorkspaceId = UUID.randomUUID().toString()
    val secondWorkspaceId = UUID.randomUUID().toString()

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("2개의 워크스페이스가 주어지고") {
        val user = queryUserPort.findById(userId)!!
        val firstWorkspace = WorkspaceGenerator.generateWorkspace(id = firstWorkspaceId, user = user)
        val secondWorkspace = WorkspaceGenerator.generateWorkspace(id = secondWorkspaceId, user = user)
        val workspaceList = listOf(firstWorkspace, secondWorkspace)
        workspaceList.forEach {
            commandWorkspacePort.save(it)
        }

        `when`("유스케이스를 실행할때") {
            val result = getAllWorkspaceUseCase.execute()

            then("주어진 workspace가 전부 반환되어야함") {
                val firstWorkspaceResult = result.list[0]
                firstWorkspaceResult.id shouldBe firstWorkspaceId
                firstWorkspaceResult.title shouldBe firstWorkspace.title
                firstWorkspaceResult.description shouldBe firstWorkspace.description
                firstWorkspaceResult.applicationList.isEmpty() shouldBe true

                val secondWorkspaceResult = result.list[1]
                secondWorkspaceResult.id shouldBe secondWorkspaceId
                secondWorkspaceResult.title shouldBe secondWorkspace.title
                secondWorkspaceResult.description shouldBe secondWorkspace.description
                secondWorkspaceResult.applicationList.isEmpty() shouldBe true
            }
        }
    }

})