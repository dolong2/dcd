package com.dcd.server.persistence.application

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.application.repository.ApplicationRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class ApplicationPersistenceAdapterTest : BehaviorSpec({
    val applicationRepository = mockk<ApplicationRepository>()
    val applicationPersistenceAdapter = ApplicationPersistenceAdapter(applicationRepository)

    given("application, user, id가 주어지고") {
        val user = User(
            email = "testEmail",
            password = "testPassword",
            name = "testName",
            roles = mutableListOf(Role.ROLE_USER)
        )
        val application = Application(
            name = "test",
            description = "test description",
            applicationType = ApplicationType.SPRING_BOOT,
            githubUrl = "testUrl",
            owner = user
        )
        val id = application.id

        `when`("save 메서드를 실행할때") {
            every { applicationRepository.save(any()) } answers { callOriginal() }
            applicationPersistenceAdapter.save(application)
            then("repository의 save메서드가 실행되야됨") {
                verify { applicationRepository.save(any()) }
            }
        }
        `when`("delete 메서드를 실행할때") {
            every { applicationRepository.delete(any()) } returns Unit
            applicationPersistenceAdapter.delete(application)
            then("repository의 delete메서드가 실행되야됨") {
                verify { applicationRepository.delete(any()) }
            }
        }
        `when`("findAllByUser 메서드를 실행할때") {
            every { applicationRepository.findAllByOwner(any()) } returns listOf(application.toEntity())
            val result = applicationPersistenceAdapter.findAllByUser(user)
            then("application list가 반환되야함") {
                result shouldBe listOf(application)
            }
        }
        `when`("findById 메서드를 실행할때") {
            every { applicationRepository.findByIdOrNull(id) } returns application.toEntity()
            var result = applicationPersistenceAdapter.findById(id)
            then("repository의 반환값이 applicationEntity라면 결과값은 application이 반환되야함") {
                result shouldBe application
            }
            every { applicationRepository.findByIdOrNull(id) } returns null
            result = applicationPersistenceAdapter.findById(id)
            then("repository의 반홥값이 null이라면 결과값은 null이 반환되어야함") {
                result shouldBe null
            }
        }
    }
})