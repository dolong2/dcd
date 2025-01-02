package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.core.spec.style.BehaviorSpec
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CreateWorkspaceUseCaseTest(
    private val createWorkspaceUseCase: CreateWorkspaceUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryUserPort: QueryUserPort,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort
) : BehaviorSpec({
    val userId = "user1"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("request가 주어지고") {
        val createWorkspaceReqDto = CreateWorkspaceReqDto(
            title = "test workspace title",
            description = "test workspace description"
        )
        `when`("useCase를 실행할때") {
            createWorkspaceUseCase.execute(createWorkspaceReqDto)
            then("워크스페이스를 저장하고 네트워크를 생성해야함") {
                val user = queryUserPort.findById(userId)!!
                val workspaceList = queryWorkspacePort.findByUser(user)
                workspaceList.size shouldBe 2
                workspaceList.map { it.title } shouldContain createWorkspaceReqDto.title
                workspaceList.map { it.description } shouldContain createWorkspaceReqDto.description
            }
        }
    }
})