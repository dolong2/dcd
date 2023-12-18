package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.impl.ValidateWorkspaceOwnerServiceImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.util.*

class ValidateWorkspaceOwnerServiceTest : BehaviorSpec({
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val service = ValidateWorkspaceOwnerServiceImpl(getCurrentUserService)

    given("user와 workspace가 주어지고") {
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val workspace = Workspace(
            id = UUID.randomUUID().toString(),
            title = "workspace",
            description = "test workspace",
            owner = user
        )
        `when`("현재 인증된 유저가 workspace의 주인일때") {
            val result = service.validateOwner(user, workspace)
            then("결과값은 Unit이여야됨") {
                result shouldBe Unit
            }
        }
        `when`("현재 인증된 유저가 workspace의 주인이 아닐때") {
            val another =
                User(email = "another", password = "password", name = "another user", roles = mutableListOf(Role.ROLE_USER))
            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    service.validateOwner(another, workspace)
                }
            }
        }
    }
})