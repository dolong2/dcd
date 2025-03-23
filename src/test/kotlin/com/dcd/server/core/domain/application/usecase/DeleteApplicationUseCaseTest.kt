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
    val targetUserId = "923a6407-a5f8-4e1e-bffd-0621910ddfc8"
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
    }

    given("애플리케이션이 없을때") {
        commandApplicationPort.delete(queryApplicationPort.findById(targetApplicationId)!!)

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deleteApplicationUseCase.execute(targetApplicationId)
                }
            }
        }
    }

    given("이미 실행중인 애플리케이션이 주어지고") {
        val targetApplication = queryApplicationPort.findById(targetApplicationId)!!
        commandApplicationPort.save(targetApplication.copy(status = ApplicationStatus.RUNNING))

        `when`("usecase를 실행할때") {
            then("에러가 발생해야함") {
                shouldThrow<CanNotDeleteApplicationException> {
                    deleteApplicationUseCase.execute(targetApplicationId)
                }
            }
        }
    }
})