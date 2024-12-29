package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.spi.CommandUserPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.dcd.server.infrastructure.test.user.UserGenerator
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetUserByStatusUseCaseTest(
    private val getUserByStatusUseCase: GetUserByStatusUseCase,
    private val commandUserPort: CommandUserPort
) : BehaviorSpec({
    val pendingUser = UserGenerator.generateUser(status = Status.PENDING)

    beforeSpec {
        commandUserPort.save(pendingUser)
    }
    afterSpec {
        commandUserPort.delete(pendingUser)
    }

    given("CREATED status가 주어지고") {
        val givenStatus = Status.CREATED

        `when`("execute 메서드를 실행할때") {
            val result = getUserByStatusUseCase.execute(givenStatus)

            then("주어진 status를 가지고 있는 유저가 조회되어야함") {
                result.list.size shouldBe 2
                result.list.forEach {
                    it.status shouldBe givenStatus
                }
            }
        }
    }

    given("PENDING status가 주어지고") {
        val givenStatus = Status.PENDING

        `when`("execute 메서드를 실행할때") {
            val result = getUserByStatusUseCase.execute(givenStatus)

            then("주어진 status를 가지고 있는 유저가 조회되어야함") {
                result.list.size shouldBe 1
                result.list.forEach {
                    it.status shouldBe givenStatus
                }
            }
        }
    }
})