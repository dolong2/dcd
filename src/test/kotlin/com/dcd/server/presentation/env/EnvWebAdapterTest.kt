package com.dcd.server.presentation.env

import com.dcd.server.core.domain.env.usecase.PutApplicationEnvUseCase
import com.dcd.server.presentation.domain.env.data.request.PutApplicationEnvRequest
import com.dcd.server.presentation.domain.env.ApplicationEnvWebAdapter
import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class EnvWebAdapterTest : BehaviorSpec({
    val putApplicationEnvUseCase = mockk<PutApplicationEnvUseCase>()
    val applicationEnvWebAdapter = ApplicationEnvWebAdapter(putApplicationEnvUseCase)

    given("AddApplicationEnvRequest가 주어지고") {
        val testId = "testId"
        val request = PutApplicationEnvRequest(
            name = "testName",
            description = "testDescription",
            details = listOf(PutEnvRequest(key = "testKey", value = "testValue", encryption = false)),
            applicationIdList = listOf(testId),
            applicationLabelList = null
        )
        `when`("addApplicationEnv메서드를 실행할때") {
            every { putApplicationEnvUseCase.execute(any()) } returns Unit
            val result = applicationEnvWebAdapter.putApplicationEnv(testId, request)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})