package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.CreateApplicationUseCase
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class ApplicationWebAdapterTest : BehaviorSpec({
    val createApplicationUseCase = mockk<CreateApplicationUseCase>()
    val applicationWebAdapter = ApplicationWebAdapter(createApplicationUseCase)

    given("CreateApplicationRequest가 주어지고") {
        val request = CreateApplicationRequest(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
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
})