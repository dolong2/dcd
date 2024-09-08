package com.dcd.server.persistence.user

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.user.repository.UserRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class UserPersistenceAdapterTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val adapter = UserPersistenceAdapter(userRepository)

    given("User가 주어질때") {
        val testUserId = "testId"
        val testEmail = "test"
        val testPassword = "test123!@#"
        val testName = "test"
        val user = User(email = "another", password = "password", name = "another user", roles = mutableListOf(Role.ROLE_USER), status = Status.CREATED)

        `when`("save 메서드를 사용할때") {
            every { userRepository.save(any()) } answers { callOriginal() }
            adapter.save(user)
            then("user.toEntity() 메서드가 호출되고 userRepository.save()가 호출되야함") {
                verify { userRepository.save(any()) }
            }
        }

        `when`("findById 메서드를 사용할때") {
            every { userRepository.findByIdOrNull(testUserId) } returns user.toEntity()
            var result = adapter.findById(testUserId)
            then("해당 유저가 존재한다면 해당 유저가 조회되어야힘") {
                result shouldBe user
            }

            every { userRepository.findByIdOrNull(testUserId) } returns null
            result = adapter.findById(testUserId)
            then("해당 유저가 존재하지 않는다면 null이 반환되어야함") {
                result shouldBe null
            }
        }

        `when`("existsById 메서드를 사용할때") {
            every { userRepository.existsById(testUserId) } returns true
            var result = adapter.exitsById(testUserId)
            then("해당 유저가 존재한다면 해당 true가 반환되어야힘") {
                result shouldBe true
            }

            every { userRepository.existsById(testUserId) } returns false
            result = adapter.exitsById(testUserId)
            then("해당 유저가 존재하지 않는다면 false가 반환되어야함") {
                result shouldBe false
            }
        }

        `when`("findByEmail 메서드를 사용할때") {
            every { userRepository.findByEmail(testEmail) } returns user.toEntity()
            var result = adapter.findByEmail(testEmail)
            then("해당 유저가 존재한다면 해당 유저가 조회되어야힘") {
                result shouldBe user
            }

            every { userRepository.findByEmail(testEmail) } returns null
            result = adapter.findByEmail(testEmail)
            then("해당 유저가 존재하지 않는다면 true가 반환되어야함") {
                result shouldBe null
            }
        }

        `when`("existsByEmail 메서드를 사용할때") {
            every { userRepository.existsByEmail(testEmail) } returns true
            var result = adapter.existsByEmail(testEmail)
            then("해당 유저가 존재한다면 해당 유저가 조회되어야힘") {
                result shouldBe true
            }

            every { userRepository.existsByEmail(testEmail) } returns false
            result = adapter.existsByEmail(testEmail)
            then("해당 유저가 존재하지 않는다면 false가 반환되어야함") {
                result shouldBe false
            }
        }
    }
})