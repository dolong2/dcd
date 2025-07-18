package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.domain.dto.request.ConnectDomainReqDto
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.application.ApplicationGenerator
import util.domain.DomainGenerator
import util.workspace.WorkspaceGenerator
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ConnectDomainUseCaseTest(
    private val connectDomainUseCase: ConnectDomainUseCase,
    private val commandDomainPort: CommandDomainPort,
    private val queryDomainPort: QueryDomainPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
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

    given("연결할 도메인이 주어지고") {
        val domain = DomainGenerator.generateDomain(id = domainId, workspace = workspaceInfo.workspace!!)
        commandDomainPort.save(domain)

        `when`("도메인에 애플리케이션을 연결할때") {
            val request = ConnectDomainReqDto(applicationId)

            connectDomainUseCase.execute(domainId, request)

            then("Nginx 설정 파일을 생성해야함") {
                val application = queryApplicationPort.findById(applicationId)!!
                val webServerConfig = FileContent.getApplicationHttpConfig(application, domain.getDomainName())
                val httpConfigDirectory = "./nginx/conf/${domain.id}"
                verify { commandPort.executeShellCommand("mkdir -p $httpConfigDirectory && cat <<'EOF' > ${httpConfigDirectory}/${application.name.replace(" ", "-")}-http.conf \n ${webServerConfig}EOF") }
            }
            then("도메인에 해당 애플리케이션이 연결되어야함") {
                val domain = queryDomainPort.findById(domainId)!!
                domain.application?.id shouldBe applicationId
            }
            then("nginx 재실행이 명령되어야함") {
                verify { commandPort.executeShellCommand("docker restart dcd-nginx") }
            }
        }

        `when`("해당 도메인이 존재하지 않을때") {
            commandDomainPort.delete(domain)
            val request = ConnectDomainReqDto(applicationId)

            then("에러가 발생해야함") {
                shouldThrow<DomainNotFoundException> {
                    connectDomainUseCase.execute(domainId, request)
                }
            }

            commandDomainPort.save(domain)
        }

        `when`("타겟 애플리케이션이 존재하지 않을때") {
            val request = ConnectDomainReqDto(UUID.randomUUID().toString())

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    connectDomainUseCase.execute(domainId, request)
                }
            }
        }

        `when`("타겟 도메인이 해당 워크스페이스랑 다른 워크스페이스에 위치할때") {
            val existingWorkspace = workspaceInfo.workspace!!
            val otherWorkspace = WorkspaceGenerator.generateWorkspace(user = existingWorkspace.owner)
            commandWorkspacePort.save(otherWorkspace)
            workspaceInfo.workspace = otherWorkspace

            val request = ConnectDomainReqDto(applicationId)

            then("에러가 발생해야함") {
                shouldThrow<DomainNotFoundException> {
                    connectDomainUseCase.execute(domainId, request)
                }
            }

            workspaceInfo.workspace = existingWorkspace
        }

        `when`("타겟 애플리케이션이 해당 워크스페이스랑 다른 워크스페이스에 위치할때") {
            val otherWorkspace = WorkspaceGenerator.generateWorkspace(user = workspaceInfo.workspace!!.owner)
            commandWorkspacePort.save(otherWorkspace)
            val otherApplication = ApplicationGenerator.generateApplication(workspace = otherWorkspace)
            commandApplicationPort.save(otherApplication)

            val request = ConnectDomainReqDto(otherApplication.id)

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    connectDomainUseCase.execute(domainId, request)
                }
            }
        }
    }
})