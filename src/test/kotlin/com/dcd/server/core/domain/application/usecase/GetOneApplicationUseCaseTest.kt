package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetOneApplicationUseCaseTest(
    private val getOneApplicationUseCase: GetOneApplicationUseCase,
    private val commandApplicationPort: CommandApplicationPort,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort
) : BehaviorSpec({

    given("애플리케이션이 주어지고") {
        val user = queryUserPort.findById("user2")!!
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        commandWorkspacePort.save(workspace)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)
        commandApplicationPort.save(application)

        `when`("해당 애플리케이션이 있을때") {
            val result = getOneApplicationUseCase.execute(application.id)

            then("result는 application의 내용이랑 같아야함") {
                result shouldBeEqualToComparingFields application.toDto()
            }
        }
    }

    given("애플리케이션이 주어지지 않고") {

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    getOneApplicationUseCase.execute("notFoundId")
                }
            }
        }
    }
})