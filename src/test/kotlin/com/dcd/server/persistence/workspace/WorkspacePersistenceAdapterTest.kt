package com.dcd.server.persistence.workspace

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.user.entity.UserJpaEntity
import com.dcd.server.persistence.workspace.adapter.toEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import com.dcd.server.persistence.workspace.repository.WorkspaceRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class WorkspacePersistenceAdapterTest : BehaviorSpec({
    val workspaceRepository = mockk<WorkspaceRepository>()
    val workspacePersistenceAdapter = WorkspacePersistenceAdapter(workspaceRepository)

    given("workspace, user가 주어지고") {
        val user = User(
            email = "testEmail",
            password = "testPassword",
            name = "testName",
            roles = mutableListOf(Role.ROLE_USER),
            status = Status.CREATED
        )

        val workspace = Workspace(
            UUID.randomUUID().toString(),
            title = "test workspace",
            description = "test workspace description",
            owner = user
        )

        val id = workspace.id

        `when`("save 메서드를 실행할때") {
            every { workspaceRepository.save(any() as WorkspaceJpaEntity) } returns workspace.toEntity()
            workspacePersistenceAdapter.save(workspace)
            then("workspaceRepository의 save메서드를 실행해야함") {
                verify { workspaceRepository.save(any() as WorkspaceJpaEntity) }
            }
        }

        `when`("delete 메서드를 실행할때") {
            every { workspaceRepository.delete(any() as WorkspaceJpaEntity) } returns Unit
            workspacePersistenceAdapter.delete(workspace)
            then("workspaceRepository의 delete메서드를 실행해야함") {
                verify { workspaceRepository.delete(any() as WorkspaceJpaEntity) }
            }
        }

        `when`("findById 메서드를 실행할때") {
            every { workspaceRepository.findByIdOrNull(id) } returns workspace.toEntity()
            val result = workspacePersistenceAdapter.findById(id)
            then("주어진 workspace가 반환되야함") {
                result shouldBe workspace
            }
        }

        `when`("findByUser 메서드를 실행할때") {
            every { workspaceRepository.findAllByOwner(any() as UserJpaEntity) } returns listOf(workspace.toEntity())
            val result = workspacePersistenceAdapter.findByUser(user)
            then("주어진 workspace가 반환되야함") {
                result shouldBe listOf(workspace)
            }
        }

        `when`("findAll 메서드를 실행할때") {
            every { workspaceRepository.findAll() } returns listOf(workspace.toEntity())
            val result = workspacePersistenceAdapter.findAll()
            then("주어진 workspace가 반환되야함") {
                result shouldBe listOf(workspace)
            }
        }
    }
})