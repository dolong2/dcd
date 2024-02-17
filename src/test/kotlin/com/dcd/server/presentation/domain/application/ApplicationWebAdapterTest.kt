package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.dto.request.GenerateSSLCertificateReqDto
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.dto.response.AvailableVersionResDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.*
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.application.data.request.AddApplicationEnvRequest
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.GenerateSSLCertificateRequest
import com.dcd.server.presentation.domain.application.data.request.UpdateApplicationRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus

class ApplicationWebAdapterTest : BehaviorSpec({
    val createApplicationUseCase = mockk<CreateApplicationUseCase>()
    val springRunApplicationUseCase = mockk<RunApplicationUseCase>()
    val getAllApplicationUseCase = mockk<GetAllApplicationUseCase>()
    val getOneApplicationUseCase = mockk<GetOneApplicationUseCase>()
    val addApplicationEnvUseCase = mockk<AddApplicationEnvUseCase>()
    val deleteApplicationEnvUseCase = mockk<DeleteApplicationEnvUseCase>()
    val stopApplicationUseCase = mockk<StopApplicationUseCase>()
    val deleteApplicationUseCase = mockk<DeleteApplicationUseCase>()
    val updateApplicationUseCase = mockk<UpdateApplicationUseCase>(relaxUnitFun = true)
    val getAvailableVersionUseCase = mockk<GetAvailableVersionUseCase>()
    val generateSSLCertificateUseCase = mockk<GenerateSSLCertificateUseCase>(relaxUnitFun = true)
    val applicationWebAdapter = ApplicationWebAdapter(createApplicationUseCase, springRunApplicationUseCase, getAllApplicationUseCase, getOneApplicationUseCase, addApplicationEnvUseCase, deleteApplicationEnvUseCase, stopApplicationUseCase, deleteApplicationUseCase, updateApplicationUseCase, getAvailableVersionUseCase, generateSSLCertificateUseCase)

    given("CreateApplicationRequest가 주어지고") {
        val request = CreateApplicationRequest(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            port = 8080,
            version = "17",
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
        `when`("runApplication 메서드를 실행할때") {
            every { springRunApplicationUseCase.execute(id) } returns Unit
            val result = applicationWebAdapter.runApplication(id)
            then("상태코드가 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("ApplicationListResponse가 주어지고") {
        val applicationResponse = ApplicationResDto(
            id = "testId",
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            port = 8080
        )
        val list = listOf(applicationResponse)
        val responseDto = ApplicationListResDto(list)
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
        val applicationResponse = ApplicationResDto(
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

    given("애플리케이션 id와 삭제할 키가 주어지고") {
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

        `when`("deleteApplication 메서드를 실행할때") {
            every { deleteApplicationUseCase.execute(testId) } returns Unit
            val result = applicationWebAdapter.deleteApplication(testId)
            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("UpdateRequest가 주어지고") {
        val testId ="testId"
        val request = UpdateApplicationRequest(name = "update", description = null, applicationType = ApplicationType.SPRING_BOOT, githubUrl = null, version = "11", port = 8080)

        `when`("updateApplication 메서드를 실행할때") {
            val result = applicationWebAdapter.updateApplication(testId, request)

            then("status는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
            then("updateApplicationUseCase를 실행해야함") {
                verify { updateApplicationUseCase.execute(testId, any() as UpdateApplicationReqDto) }
            }
        }
    }

    given("애플리케이션 타입이 주어지고") {
        val applicationType = ApplicationType.SPRING_BOOT

        `when`("getAvailableVersion 메서드를 실행할때") {
            val availableVersionResDto = AvailableVersionResDto(version = listOf("11", "17"))
            every { getAvailableVersionUseCase.execute(applicationType) } returns availableVersionResDto

            val result = applicationWebAdapter.getAvailableVersion(applicationType)
            then("result의 바디는 availableVersionResDto랑 같아야함") {
                result.body?.version shouldBe availableVersionResDto.version
            }
            then("result는 200 상태코드를 가져야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("애플리케이션 id와 GenerateSSLCertificateRequest가 주어지고") {
        val testId ="testId"
        val request = GenerateSSLCertificateRequest("test.domain.com")

        `when`("generateSSLCertificate 메서드를 실행할때") {
            val result = applicationWebAdapter.generateSSLCertificate(testId, request)

            then("GenerateSSLCertificateUseCase를 실행해야함") {
                verify { generateSSLCertificateUseCase.execute(testId, any() as GenerateSSLCertificateReqDto) }
            }
            then("result는 200 상태코드를 가져야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})