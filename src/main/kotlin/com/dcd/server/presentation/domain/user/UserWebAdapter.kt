package com.dcd.server.presentation.domain.user

import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.usecase.ChangePasswordUseCase
import com.dcd.server.core.domain.user.usecase.ChangeUserStatusUseCase
import com.dcd.server.core.domain.user.usecase.GetUserByStatusUseCase
import com.dcd.server.core.domain.user.usecase.GetUserProfileUseCase
import com.dcd.server.presentation.domain.user.data.exetension.toDto
import com.dcd.server.presentation.domain.user.data.exetension.toResponse
import com.dcd.server.presentation.domain.user.data.request.PasswordChangeRequest
import com.dcd.server.presentation.domain.user.data.response.UserListResponse
import com.dcd.server.presentation.domain.user.data.response.UserProfileResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserWebAdapter(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val changeUserStatusUseCase: ChangeUserStatusUseCase,
    private val getUserStatusUseCase: GetUserByStatusUseCase
) {
    @GetMapping("/profile")
    fun getUserProfile(): ResponseEntity<UserProfileResponse> =
        getUserProfileUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse()) }

    @PatchMapping("/password")
    fun changePassword(@RequestBody passwordChangeRequest: PasswordChangeRequest): ResponseEntity<Void> =
        changePasswordUseCase.execute(passwordChangeRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{userId}/status")
    fun updateStatus(@PathVariable userId: String, @RequestParam status: Status): ResponseEntity<Void> =
        changeUserStatusUseCase.execute(userId, status)
            .run { ResponseEntity.ok().build() }

    @GetMapping
    fun getUserByStatus(@RequestParam status: Status): ResponseEntity<UserListResponse> =
        getUserStatusUseCase.execute(status)
            .let { ResponseEntity.ok(it.toResponse()) }
}