package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
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
class AddApplicationEnvUseCaseTest(
    private val addApplicationEnvUseCase: AddApplicationEnvUseCase,
    private val queryUserPort: QueryUserPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) : BehaviorSpec({
    var targetApplicationId = ""
    beforeSpec {
        val targetUser = queryUserPort.findById("user1")!!

    given("request가 주어지고") {
        val workspace = queryWorkspacePort.findByUser(targetUser).first()
        val application = queryApplicationPort.findAllByWorkspace(workspace).first()
        targetApplicationId = application.id
    }

        val request = AddApplicationEnvReqDto(
            envList = mapOf(Pair("testA", "testB"))
        )
        val application = ApplicationGenerator.generateApplication()
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(application.id) } returns application
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            addApplicationEnvUseCase.execute(application.id, request)
            then("commandApplicationPort의 save메서드로 업데이트 해야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
        `when`("만약 해당 id인 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(application.id) } returns null
            then("ApplicationNotFoundException을 던져야함") {
                shouldThrow<ApplicationNotFoundException> {
                    addApplicationEnvUseCase.execute(application.id, request)
                }
            }
        }
    }
})