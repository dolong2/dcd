package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.domain.exception.AlreadyConnectedDomainException
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.application.ApplicationGenerator
import util.domain.DomainGenerator
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteDomainUseCaseTest(
    private val deleteDomainUseCase: DeleteDomainUseCase,
    private val commandDomainPort: CommandDomainPort,
    private val queryDomainPort: QueryDomainPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandApplicationPort: CommandApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val domainId = UUID.randomUUID().toString()

    given("삭제할 도메인이 주어지고") {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        workspaceInfo.workspace = workspace
        val domain = DomainGenerator.generateDomain(id = domainId, workspace = workspace)
        commandDomainPort.save(domain)

        `when`("도메인을 삭제할때") {
            deleteDomainUseCase.execute(domainId)

            then("도메인을 조회할 수 없게 되야함") {
                queryDomainPort.findById(domainId) shouldBe null
            }
        }

        `when`("현재 워크스페이스 정보가 도메인의 워크스페이스가 아닐때") {
            val otherWorkspace = workspace.copy(owner = workspace.owner)
            commandWorkspacePort.save(otherWorkspace)
            workspaceInfo.workspace = workspace

            then("에러가 발생해야함") {
                shouldThrow<DomainNotFoundException> {
                    deleteDomainUseCase.execute(domainId)
                }
            }

            workspaceInfo.workspace = workspace
        }

        `when`("이미 연결되어있는 도메인일때") {
            val application = ApplicationGenerator.generateApplication(workspace = workspace)
            commandApplicationPort.save(application)
            val updatedDomain = domain.copy(application = application)
            commandDomainPort.save(updatedDomain)

            then("도메인을 삭제하면 예외가 발생해야함") {
                shouldThrow<AlreadyConnectedDomainException> {
                    deleteDomainUseCase.execute(domainId)
                }
            }
        }
    }

    given("삭제할 도메인이 주어지지않고") {

        `when`("도메인을 삭제할때") {

            then("예외가 발생해야함") {
                shouldThrow<DomainNotFoundException> {
                    deleteDomainUseCase.execute(domainId)
                }
            }
        }
    }
})