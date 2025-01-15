package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UpdateApplicationUseCaseTest(
    private val updateApplicationUseCase: UpdateApplicationUseCase,
    private val queryUserPort: QueryUserPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort
) : BehaviorSpec({
    val targetUserId = "user1"

    val updateReqDto = UpdateApplicationReqDto(name = "updated application", description = "dldl", applicationType = ApplicationType.SPRING_BOOT, githubUrl = null, version = "11", port = 8080)

    given("애플리케이션 아이디가 주어지고") {
        val targetUser = queryUserPort.findById(targetUserId)!!
        val workspace = queryWorkspacePort.findByUser(targetUser).first()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace)
        val applicationId = applicationList.first().id

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
        }
    }

    given("애플리케이션이 주어지지 않고") {

        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    updateApplicationUseCase.execute(applicationId, updateReqDto)
                }
            }
        }
    }

    given("실행중인 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(id = applicationId, workspace = workspace, status = ApplicationStatus.RUNNING)

        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns application

            then("ReqDto의 내용이 반영된 애플리케이션을 저장해야함") {
                shouldThrow<AlreadyRunningException> {
                    updateApplicationUseCase.execute(applicationId, updateReqDto)
                }
            }
        }
    }
})