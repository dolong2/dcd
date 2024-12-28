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

        `when`("해당 유저가 존재할때") {
            val user = UserGenerator.generateUser()

            every { queryUserPort.findById(userId) } returns user

            changeUserStatusUseCase.execute(userId, status)

            then("user가 수정되고 저장됐는지 검사") {
                verify { queryUserPort.findById(userId) }
                verify { commandUserPort.save(user.copy(status = status)) }
            }
        }

        `when`("해당 userId를 가진 유저가 없을때") {
            every { queryUserPort.findById(userId) } returns null

            then("UserNotFoundException이 발생해야함") {
                shouldThrow<UserNotFoundException> {
                    changeUserStatusUseCase.execute(userId, status)
                }
            }
        }
    }
})