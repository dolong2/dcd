package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.domain.dto.extension.toResDto
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.domain.DomainGenerator
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetDomainUseCaseTest(
    private val getDomainUseCase: GetDomainUseCase,
    private val commandDomainPort: CommandDomainPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val domainId = UUID.randomUUID().toString()

    given("도메인이 주어지고") {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        workspaceInfo.workspace = workspace
        val domain = DomainGenerator.generateDomain(id = domainId, workspace = workspace)
        commandDomainPort.save(domain)

        `when`("조회할때") {
            val result = getDomainUseCase.execute()

            then("주어진 도메인의 정보가 응답되야함") {
                result.list.size shouldBe 1

                result.list[0] shouldBe domain.toResDto()
            }
        }

        `when`("워크스페이스 정보가 초기화되지 않았을때") {
            workspaceInfo.workspace = null

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    getDomainUseCase.execute()
                }
            }
        }
    }
})