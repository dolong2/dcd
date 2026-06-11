package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationInitialScriptPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coVerify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UpdateApplicationUseCaseTest(
    private val updateApplicationUseCase: UpdateApplicationUseCase,
    private val queryUserPort: QueryUserPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val queryApplicationInitialScriptPort: QueryApplicationInitialScriptPort,
    private val commandApplicationPort: CommandApplicationPort,
    @MockkBean(relaxed = true)
    private val containerPort: ContainerPort,
) : BehaviorSpec({
    val targetUserId = "923a6407-a5f8-4e1e-bffd-0621910ddfc8"

    val updateReqDto = UpdateApplicationReqDto(name = "testName", description = "dldl", applicationType = ApplicationType.SPRING_BOOT, githubUrl = null, version = "11", port = 8080, initialScripts = listOf("echo test"))

    given("애플리케이션 아이디가 주어지고") {
        val targetUser = queryUserPort.findById(targetUserId)!!
        val workspace = queryWorkspacePort.findByUser(targetUser).first()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace)
        val targetApplication = applicationList.first()
        val applicationId = targetApplication.id

        `when`("usecase를 실행할때") {
            updateApplicationUseCase.execute(applicationId, updateReqDto)

            then("ReqDto의 내용이 반영된 애플리케이션을 저장해야함") {
                val result = queryApplicationPort.findById(applicationId)
                result shouldNotBe null
                result?.name shouldBe updateReqDto.name
                result?.description shouldBe updateReqDto.description
                result?.applicationType shouldBe updateReqDto.applicationType
                result?.port shouldBe updateReqDto.port
                result?.githubUrl shouldBe updateReqDto.githubUrl
                result?.version shouldBe updateReqDto.version
            }

            then("변경된 초기화 스크립트를 가지고 있어야함") {
                val result = queryApplicationPort.findById(applicationId).also { it shouldNotBe null }!!
                val initialScripts = queryApplicationInitialScriptPort.findAllByApplication(result)
                initialScripts shouldHaveSize 1
                initialScripts[0].script shouldBe updateReqDto.initialScripts.first()
            }
        }

        `when`("애플리케이션이 실행중이라면") {
            commandApplicationPort.save(targetApplication.copy(status = ApplicationStatus.RUNNING))

            then("에러가 발생해야함") {
                shouldThrow<AlreadyRunningException> {
                    updateApplicationUseCase.execute(applicationId, updateReqDto)
                }
            }
        }
    }

    given("존재하지 않는 애플리케이션 아이디가 주어지고") {
        val notFoundApplicationId = UUID.randomUUID().toString()

        `when`("usecase를 실행할때") {

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    updateApplicationUseCase.execute(notFoundApplicationId, updateReqDto)
                }
            }
        }
    }
})