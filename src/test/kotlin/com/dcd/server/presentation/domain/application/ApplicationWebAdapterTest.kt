package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.dto.response.ApplicationListResponseDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResponseDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.*
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.application.data.request.AddApplicationEnvRequest
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.RunApplicationRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class ApplicationWebAdapterTest : BehaviorSpec({
    val createApplicationUseCase = mockk<CreateApplicationUseCase>()
    val springApplicationRunUseCase = mockk<ApplicationRunUseCase>()
    val getAllApplicationUseCase = mockk<GetAllApplicationUseCase>()
    val getOneApplicationUseCase = mockk<GetOneApplicationUseCase>()
    val addApplicationEnvUseCase = mockk<AddApplicationEnvUseCase>()
    val deleteApplicationEnvUseCase = mockk<DeleteApplicationEnvUseCase>()
    val stopApplicationUseCase = mockk<StopApplicationUseCase>()
    val deleteApplicationUseCase = mockk<DeleteApplicationUseCase>()
    val applicationWebAdapter = ApplicationWebAdapter(createApplicationUseCase, springApplicationRunUseCase, getAllApplicationUseCase, getOneApplicationUseCase, addApplicationEnvUseCase, deleteApplicationEnvUseCase, stopApplicationUseCase, deleteApplicationUseCase)

    given("CreateApplicationRequest가 주어지고") {
        val request = CreateApplicationRequest(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            port = 8080
        )

        `when`("createApplication 메서드를 실행할때") {
            every { createApplicationUseCase.execute("testWorkspaceId", any()) } returns Unit
            val result = applicationWebAdapter.createApplication("testWorkspaceId", request)
            then("상태코드가 201이여야함") {
                result.statusCode shouldBe HttpStatus.CREATED
            }
        }
    }

    given("RunApplicationRequest가 주어지고") {
        val id = "testApplicationId"
        val request = RunApplicationRequest(langVersion = 11, env = mapOf())
        `when`("runApplication 메서드를 실행할때") {
            every { springApplicationRunUseCase.execute(id, any()) } returns Unit
            val result = applicationWebAdapter.runApplication(id, request)
            then("상태코드가 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("ApplicationListResponse가 주어지고") {
        val applicationResponse = ApplicationResponseDto(
            id = "testId",
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            port = 8080
        )
        val list = listOf(applicationResponse)
        val responseDto = ApplicationListResponseDto(list)
        `when`("getAllApplication 메서드를 실행할때") {
            every { getAllApplicationUseCase.execute("testWorkspaceId") } returns responseDto
            val response = applicationWebAdapter.getAllApplication("testWorkspaceId")
            then("응답바디는 targetResponse와 같아야하고 status는 200이여야함") {
                val targetResponse = responseDto.toResponse()
                response.body shouldBe targetResponse
                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("ApplicationResponseDto가 주어지고") {
        val testId = "testId"
        val applicationResponse = ApplicationResponseDto(
            id = testId,
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            port = 8080
        )
        `when`("getOneApplication 메서드를 실행할때") {
            every { getOneApplicationUseCase.execute(testId) } returns applicationResponse
            val response = applicationWebAdapter.getOneApplication(testId)
            then("응답바디는 targetResponse와 같아야하고 status는 200이여야함") {
                val targetResponse = applicationResponse.toResponse()
                response.body shouldBe targetResponse
                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("AddApplicationEnvRequest가 주어지고") {
        val testId = "testId"
        val request = AddApplicationEnvRequest(
            envList = mapOf(Pair("testKey", "testValue"))
        )
        `when`("addApplicationEnv메서드를 실행할때") {
            every { addApplicationEnvUseCase.execute(testId, any()) } returns Unit
            val result = applicationWebAdapter.addApplicationEnv(testId, request)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("애플리케이션 id와 삭젷할 키가 주어지고") {
        val testId = "testId"
        val key = "testKey"
        `when`("deleteApplicationEnv메서드를 실행할때") {
            every { deleteApplicationEnvUseCase.execute(testId, key) } returns Unit
            val result = applicationWebAdapter.deleteApplicationEnv(testId, key)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("애플리케이션 id가 주어지고") {
        val testId = "testId"
        `when`("stopApplication 메서드를 실행할때") {
            every { stopApplicationUseCase.execute(testId) } returns Unit
            val result = applicationWebAdapter.stopApplication(testId)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("애플리케이션 Id가 주어지고") {
        val testId = "testId"
        `when`("deleteApplication 메서드를 실행할때") {
            every { deleteApplicationUseCase.execute(testId) } returns Unit
            val result = applicationWebAdapter.deleteApplication(testId)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})