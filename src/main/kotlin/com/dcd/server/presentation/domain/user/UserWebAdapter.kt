package com.dcd.server.presentation.domain.user

import com.dcd.server.core.domain.user.usecase.GetUserProfileUseCase
import com.dcd.server.presentation.domain.user.data.exetension.toResponse
import com.dcd.server.presentation.domain.user.data.response.UserProfileResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserWebAdapter(
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    @GetMapping("/profile")
    fun getUserProfile(): ResponseEntity<UserProfileResponse> =
        getUserProfileUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse()) }
}