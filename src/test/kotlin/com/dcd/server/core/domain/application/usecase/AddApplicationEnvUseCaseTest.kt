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

        val workspace = queryWorkspacePort.findByUser(targetUser).first()
        val application = queryApplicationPort.findAllByWorkspace(workspace).first()
        targetApplicationId = application.id
    }

    given("애플리케이션 아이디와 request가 주어지고") {
        val request = AddApplicationEnvReqDto(
            envList = mapOf(Pair("testA", "testB"))
        )
        `when`("usecase를 실행할때") {
            addApplicationEnvUseCase.execute(targetApplicationId, request)
            then("타겟 애플리케이션에 환경변수가 추가되어야함") {
                val result = queryApplicationPort.findById(targetApplicationId)
                result shouldNotBe null
                result!!.env["testA"] shouldNotBe null
                result.env["testA"] shouldBe "testB"
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