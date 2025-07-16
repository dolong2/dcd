package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.domain.DomainGenerator
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DisconnectDomainUseCaseTest(
    private val disconnectDomainUseCase: DisconnectDomainUseCase,
    private val commandDomainPort: CommandDomainPort,
    private val queryDomainPort: QueryDomainPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
) : BehaviorSpec({
    val domainId = UUID.randomUUID().toString()
    val applicationId = "2fb0f315-8272-422f-8e9f-c4f765c022b2"
    beforeContainer {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        workspaceInfo.workspace = workspace
    }

    given("연결해제할 도메인이 주어지고") {
        val application = queryApplicationPort.findById(applicationId)!!
        val domain = DomainGenerator.generateDomain(id = domainId, workspace = workspaceInfo.workspace!!, application = application)
        commandDomainPort.save(domain)

        `when`("도메인에 애플리케이션을 연결 해제할때") {
            disconnectDomainUseCase.execute(domainId)

            then("Nginx 설정 파일을 삭제해야함") {
                val httpConfigDirectory = "./nginx/conf/${domain.id}"
                verify { commandPort.executeShellCommand("rm -r $httpConfigDirectory") }
            }
            then("도메인에 해당 애플리케이션이 null로 변경되어야함") {
                val domain = queryDomainPort.findById(domainId)!!
                domain.application shouldBe null
            }
            then("nginx 재실행이 명령되어야함") {
                verify { commandPort.executeShellCommand("docker restart dcd-nginx") }
            }
        }

        `when`("해당 도메인이 존재하지 않을때") {
            commandDomainPort.delete(domain)

            then("에러가 발생해야함") {
                shouldThrow<DomainNotFoundException> {
                    disconnectDomainUseCase.execute(domainId)
                }
            }

            commandDomainPort.save(domain)
        }
    }
})