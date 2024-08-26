package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.model.Status
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.user.UserGenerator

class ChangeUserStatusUseCaseTest : BehaviorSpec({
    val queryUserPort = mockk<QueryUserPort>()
    val commandUserPort = mockk<CommandUserPort>(relaxUnitFun = true)
    val changeUserStatusUseCase = ChangeUserStatusUseCase(queryUserPort, commandUserPort)

    given("userId, 변경할 status가 주어지고") {
        val userId = "testUserId"
        val status = Status.CREATED

        `when`("해당 유저가 존재할때") {
            val user = UserGenerator.generateUser()

            every { queryUserPort.findById(userId) } returns user

            changeUserStatusUseCase.execute(userId, status)

            then("user가 수정되고 저장됐는지 검사") {
                verify { queryUserPort.findById(userId) }
                verify { commandUserPort.save(user.copy(status = status)) }
            }
        }
    }
})