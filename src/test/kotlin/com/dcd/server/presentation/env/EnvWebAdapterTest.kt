package com.dcd.server.presentation.env

import com.dcd.server.core.domain.env.dto.response.ApplicationEnvDetailResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvListResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvSimpleResDto
import com.dcd.server.core.domain.env.usecase.DeleteApplicationEnvUseCase
import com.dcd.server.core.domain.env.usecase.GetApplicationEnvUseCase
import com.dcd.server.core.domain.env.usecase.PutApplicationEnvUseCase
import com.dcd.server.presentation.domain.env.data.request.PutApplicationEnvRequest
import com.dcd.server.presentation.domain.env.ApplicationEnvWebAdapter
import com.dcd.server.presentation.domain.env.data.extension.toResponse
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
    val getApplicationEnvUseCase = mockk<GetApplicationEnvUseCase>()
    val applicationEnvWebAdapter = ApplicationEnvWebAdapter(putApplicationEnvUseCase, deleteApplicationEnvUseCase, getApplicationEnvUseCase)

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

        `when`("이미 존재하는 환경변수 일때") {
            val targetEnvId = UUID.randomUUID()
            every { putApplicationEnvUseCase.execute(any()) } returns Unit
            val result = applicationEnvWebAdapter.updateApplicationEnv(testId, targetEnvId, request)

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

    given("환경변수 응답이 주어지고") {
        val testId = "testId"
        val applicationEnvResDto = ApplicationEnvSimpleResDto(id = UUID.randomUUID(), name = "testName", description = "testDescription")
        val applicationEnvListResDto = ApplicationEnvListResDto(listOf(applicationEnvResDto))
        every { getApplicationEnvUseCase.execute() } returns applicationEnvListResDto

        `when`("환경변수 조회 메서드를 실행할때") {
            val result = applicationEnvWebAdapter.getApplicationEnvList(testId)

            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
            then("응답값은 주어진 응답과 일치해야함") {
                result.body shouldBe applicationEnvListResDto.toResponse()
            }
        }
    }

    given("환경변수 상세 응답이 주어지고") {
        val testId = "testId"
        val envId = UUID.randomUUID()
        val expectedEnvRes = ApplicationEnvResDto(
            id = envId,
            name = "testName",
            description = "testDescription",
            details = listOf(ApplicationEnvDetailResDto(key = "testKey", value = "testValue", encryption = false))
        )
        every { getApplicationEnvUseCase.execute(envId) } returns expectedEnvRes

        `when`("환경변수 상세조회 메서드를 실행할때") {
            val result = applicationEnvWebAdapter.getApplicationEnv(testId, envId)

            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
            then("응답값은 주어진 응답과 일치해야함") {
                result.body shouldBe expectedEnvRes.toResponse()
            }
        }
    }
})