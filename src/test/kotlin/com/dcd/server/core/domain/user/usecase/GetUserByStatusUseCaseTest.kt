package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.user.UserGenerator

class GetUserByStatusUseCaseTest : BehaviorSpec({
    val queryUserPort = mockk<QueryUserPort>()
    val getUserByStatusUseCase = GetUserByStatusUseCase(queryUserPort)

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