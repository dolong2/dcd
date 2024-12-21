package com.dcd.server.presentation.domain.user

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.dto.response.UserListResDto
import com.dcd.server.core.domain.user.dto.response.UserProfileResDto
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.usecase.ChangePasswordUseCase
import com.dcd.server.core.domain.user.usecase.ChangeUserStatusUseCase
import com.dcd.server.core.domain.user.usecase.GetUserByStatusUseCase
import com.dcd.server.core.domain.user.usecase.GetUserProfileUseCase
import com.dcd.server.presentation.domain.user.data.exetension.toResponse
import com.dcd.server.presentation.domain.user.data.request.PasswordChangeRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import com.dcd.server.infrastructure.test.user.UserGenerator

class UserWebAdapterTest : BehaviorSpec({
    val getUserProfileUseCase = mockk<GetUserProfileUseCase>()
    val changePasswordUseCase = mockk<ChangePasswordUseCase>(relaxUnitFun = true)
    val changeUserStatusUseCase = mockk<ChangeUserStatusUseCase>(relaxUnitFun = true)
    val getUserByStatusUseCase = mockk<GetUserByStatusUseCase>()

    val userWebAdapter = UserWebAdapter(getUserProfileUseCase, changePasswordUseCase, changeUserStatusUseCase, getUserByStatusUseCase)

    given("UserProfileResDto가 주어지고") {
        val user =
            User(email = "another", password = "password", name = "another user", roles = mutableListOf(Role.ROLE_USER), status = Status.CREATED)
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

    given("PasswordChangeRequest가 주어지고") {
        val passwordChangeRequest =
            PasswordChangeRequest(existingPassword = "existingPassword", newPassword = "newPassword")

        `when`("changePassword 메서드를 실행할떼") {

            val response = userWebAdapter.changePassword(passwordChangeRequest)

            then("응답 코드는 200이여야함") {
                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("UserId, Status가 주어지고") {
        val userId = "testUserId"
        val status = Status.CREATED

        `when`("updateStatus 메서드를 실행할때") {
            val response = userWebAdapter.updateStatus(userId, status)

            then("응답코드는 200이여야함") {
                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("Status가 주어지고") {
        val status = Status.CREATED

        `when`("getUserByStatus 메서드를 실행할때") {
            val user = UserGenerator.generateUser()
            val userListResDto = UserListResDto(listOf(user.toDto()))

            every { getUserByStatusUseCase.execute(status) } returns userListResDto

            val response = userWebAdapter.getUserByStatus(status)

            then("상태코드는 200이여야하고, 바디는 userListDto의 정보랑 같아야함") {
                response.statusCode shouldBe HttpStatus.OK
                response.body shouldBe userListResDto.toResponse()
            }
        }
    }
})