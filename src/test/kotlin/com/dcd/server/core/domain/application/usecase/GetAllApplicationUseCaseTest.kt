package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetAllApplicationUseCaseTest(
    private val getAllApplicationUseCase: GetAllApplicationUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryUserPort: QueryUserPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val userId = "923a6407-a5f8-4e1e-bffd-0621910ddfc8"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        workspaceInfo.workspace = queryWorkspacePort.findByUser(queryUserPort.findById(userId)!!).first()
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("applicationList가 주어지고") {
        val workspace = queryWorkspacePort.findByUser(queryUserPort.findById(userId)!!).first()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace)

        `when`("usecase를 실행할때") {
            val result = getAllApplicationUseCase.execute(null)
            val target = ApplicationListResDto(applicationList.map { it.toDto() })
            then("result는 target이랑 같아야함") {
                result shouldBe target
            }
        }
    }
})