package com.dcd.server.presentation.env

import com.dcd.server.core.domain.env.usecase.DeleteApplicationEnvUseCase
import com.dcd.server.core.domain.env.usecase.PutApplicationEnvUseCase
import com.dcd.server.presentation.domain.env.data.request.PutApplicationEnvRequest
import com.dcd.server.presentation.domain.env.ApplicationEnvWebAdapter
import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.util.UUID

class EnvWebAdapterTest : BehaviorSpec({
    val putApplicationEnvUseCase = mockk<PutApplicationEnvUseCase>()
    val deleteApplicationEnvUseCase = mockk<DeleteApplicationEnvUseCase>(relaxUnitFun = true)
    val applicationEnvWebAdapter = ApplicationEnvWebAdapter(putApplicationEnvUseCase, deleteApplicationEnvUseCase)

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

    given("삭제할 Env 아이디가 주어지고") {
        val testId = "testId"
        val envId = UUID.randomUUID()

        `when`("환경변수 삭제 메서드를 실행할때") {
            val result = applicationEnvWebAdapter.deleteApplicationEnv(testId, envId)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})