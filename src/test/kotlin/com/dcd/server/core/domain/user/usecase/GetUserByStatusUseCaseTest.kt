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

    given("조회할 status가 주어지고") {
        val status = Status.CREATED

        `when`("execute 메서드를 실행할때") {
            val userList = listOf(UserGenerator.generateUser())
            every { queryUserPort.findByStatus(status) } returns userList
            val result = getUserByStatusUseCase.execute(status)

            then("result는 유저의 정보를 가지고 있어야함") {
                verify { queryUserPort.findByStatus(status) }
                result.list shouldBe userList.map { it.toDto() }
            }
        }
    }
})