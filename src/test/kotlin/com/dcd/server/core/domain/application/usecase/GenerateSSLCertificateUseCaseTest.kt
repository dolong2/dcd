package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.GenerateSSLCertificateReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GenerateSSLCertificateService
import com.dcd.server.core.domain.application.service.GetExternalPortService
import com.dcd.server.core.domain.application.service.PutSSLCertificateService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class GenerateSSLCertificateUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val generateSSLCertificateService = mockk<GenerateSSLCertificateService>(relaxUnitFun = true)
    val putSSLCertificateService = mockk<PutSSLCertificateService>(relaxUnitFun = true)
    val getExternalPortService = mockk<GetExternalPortService>(relaxed = true)
    val generateSSLCertificateUseCase = GenerateSSLCertificateUseCase(
        queryApplicationPort,
        generateSSLCertificateService,
        putSSLCertificateService,
        getExternalPortService
    )

    given("application id, GenerateSSLCertificateReqDto가 주어지고") {
        val applicationId = UUID.randomUUID().toString()
        val reqDto = GenerateSSLCertificateReqDto(domain = "dcd-server.dolong.co.kr")

        `when`("usecase를 실행하면") {
            val user =
                User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
            val workspace = Workspace(
                UUID.randomUUID().toString(),
                title = "test workspace",
                description = "test workspace description",
                owner = user
            )
            val application = Application(
                id = "testId",
                name = "test",
                description = "test",
                applicationType = ApplicationType.SPRING_BOOT,
                env = mapOf(),
                githubUrl = "testUrl",
                workspace = workspace,
                port = 8080,
                version = "17"
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            generateSSLCertificateUseCase.execute(applicationId, reqDto)

            then("ssl 인증서를 발급하고, 애플리케이션에 적용해야함") {
                verify { generateSSLCertificateService.generateSSL(reqDto.domain) }
                verify { putSSLCertificateService.putSSLCertificate(reqDto.domain, getExternalPortService.getExternalPort(application.port), application) }
            }

            then("주어진 id로 애플리케이션을 조회해야함") {
                verify { queryApplicationPort.findById(applicationId) }
            }

            then("외부 포트중에 사용할 수 있는 포트를 조회해야함") {
                verify { getExternalPortService.getExternalPort(application.port) }
            }
        }

        `when`("해당 id를 가진 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(applicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    generateSSLCertificateUseCase.execute(applicationId, reqDto)
                }
            }
        }
    }

})