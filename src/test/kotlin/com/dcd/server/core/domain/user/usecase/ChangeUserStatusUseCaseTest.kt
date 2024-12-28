package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.spi.QueryUserPort
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
class ChangeUserStatusUseCaseTest(
    private val changeUserStatusUseCase: ChangeUserStatusUseCase,
    private val queryUserPort: QueryUserPort
) : BehaviorSpec({

    given("존재하는 유저의 아이디가 주어지고") {
        val userId = "user1"
        val status = Status.PENDING

        `when`("유스케이스를 실행할때") {
            changeUserStatusUseCase.execute(userId, status)

            then("유저의 상태는 PENDING으로 변경되어야함") {
                val result = queryUserPort.findById(userId)
                result shouldNotBe null
                result?.status shouldBe status
            }
        }
    }
})