package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.CreateApplicationUseCase
import com.dcd.server.core.domain.application.usecase.SpringApplicationRunUseCase
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.RunApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.SpringApplicationRunRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class ApplicationWebAdapterTest : BehaviorSpec({
    val createApplicationUseCase = mockk<CreateApplicationUseCase>()
    val springApplicationRunUseCase = mockk<SpringApplicationRunUseCase>()
    val applicationWebAdapter = ApplicationWebAdapter(createApplicationUseCase, springApplicationRunUseCase)

    given("CreateApplicationRequest가 주어지고") {
        val request = CreateApplicationRequest(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl"
        )

        `when`("createApplication 메서드를 실행할때") {
            every { createApplicationUseCase.execute(any()) } returns Unit
            val result = applicationWebAdapter.createApplication(request)
            then("상태코드가 201이여야함") {
                result.statusCode shouldBe HttpStatus.CREATED
            }
        }
    }

    given("RunApplicationRequest가 주어지고") {
        val id = "testApplicationId"
        val request = SpringApplicationRunRequest(langVersion = 11, dbTypes = arrayOf())
        `when`("runApplication 메서드를 실행할때") {
            every { springApplicationRunUseCase.execute(id, any()) } returns Unit
            val result = applicationWebAdapter.runApplication(id, request)
            then("상태코드가 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})