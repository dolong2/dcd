package com.dcd.server.presentation.domain.user

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.dto.response.UserProfileResDto
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.usecase.GetUserProfileUseCase
import com.dcd.server.presentation.domain.user.data.exetension.toResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class UserWebAdapterTest : BehaviorSpec({
    val getUserProfileUseCase = mockk<GetUserProfileUseCase>()

    val userWebAdapter = UserWebAdapter(getUserProfileUseCase)

    given("UserProfileResDto가 주어지고") {
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val userProfileResDto = UserProfileResDto(user = user.toDto(), workspaces = listOf())

        `when`("getProfile 메서드를 실행할때") {
            every { getUserProfileUseCase.execute() } returns userProfileResDto

            val response = userWebAdapter.getUserProfile()

            then("응답 코드는 200이여야함") {
                response.statusCode shouldBe HttpStatus.OK
            }
            then("body값은 userProfileResDto를 담고있어야함") {
                response.body!! shouldBe userProfileResDto.toResponse()
            }
        }
    }
})