package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeleteApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteApplicationUseCaseTest(
    private val deleteApplicationUseCase: DeleteApplicationUseCase,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryUserPort: QueryUserPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort
) : BehaviorSpec({
    val targetUserId = "user1"
    var targetApplicationId = ""

    beforeSpec {
        val user = queryUserPort.findById(targetUserId)!!
        val workspace = queryWorkspacePort.findByUser(user).first()
        targetApplicationId = queryApplicationPort.findAllByWorkspace(workspace).first().id
    }

    given("애플리케이션 id가 주어지고") {

        `when`("usecase를 실행할때") {
            deleteApplicationUseCase.execute(targetApplicationId)

            then("해당 아이디를 가진 애플리케이션이 지워져야함") {
                queryApplicationPort.findById(targetApplicationId) shouldBe null
            }
        }
        `when`("application을 찾을 수 없을때") {
            every { queryApplicationPort.findById(applicationId) } returns null
            then("applicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
        `when`("현재 유저가 소유자가 아닐때") {
            then("RuntimeException이 발생해야함") {
                shouldThrow<RuntimeException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
    }

    given("이미 실행중인 애플리케이션이 주어지고") {
        val applicationId = "testId"
        val user = UserGenerator.generateUser()
        val application = ApplicationGenerator.generateApplication(workspace = WorkspaceGenerator.generateWorkspace(user = user), status = ApplicationStatus.RUNNING)
        `when`("usecase를 실행할때") {
            every { commandApplicationPort.delete(application) } returns Unit
            every { queryApplicationPort.findById(applicationId) } returns application
            then("commandApplicationPort의 delete메서드가 실행되어야함") {
                shouldThrow<CanNotDeleteApplicationException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
    }
})