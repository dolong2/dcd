package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.env.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.env.repository.ApplicationEnvDetailRepository
import com.dcd.server.persistence.env.repository.ApplicationEnvRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PutApplicationEnvUseCaseTest(
    private val putApplicationEnvUseCase: PutApplicationEnvUseCase,
    private val applicationEnvRepository: ApplicationEnvRepository,
    private val applicationEnvDetailRepository: ApplicationEnvDetailRepository,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    beforeSpec {
        applicationEnvRepository.deleteAll()
    }

    beforeTest {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")
        workspaceInfo.workspace = workspace
    }

    given("애플리케이션 아이디와 환경변수 삽입 요청 dto가 주어지고") {
        val targetApplicationId = "2fb0f315-8272-422f-8e9f-c4f765c022b2"
        val putApplicationEnvReqDto = PutApplicationEnvReqDto(
            name = "testEnv",
            description = "test",
            details = listOf(PutEnvReqDto(key = "testEnvKey", value = "testEnvValue", encryption = false)),
            applicationIdList = listOf(targetApplicationId),
            applicationLabelList = null
        )

        `when`("유스케이스를 실행하면") {
            putApplicationEnvUseCase.execute(putApplicationEnvReqDto)

            then("Env가 생성되어야함") {
                applicationEnvRepository.flush()
                val appEnvList = applicationEnvRepository.findAll()
                appEnvList.size shouldBe 1

                val appEnv = appEnvList.first()
                appEnv.name shouldBe putApplicationEnvReqDto.name
                appEnv.description shouldBe putApplicationEnvReqDto.description

                val envDetail = applicationEnvDetailRepository.findAll().first()
                envDetail.envDetail.key shouldBe putApplicationEnvReqDto.details[0].key
                envDetail.envDetail.value shouldBe putApplicationEnvReqDto.details[0].value
                envDetail.envDetail.encryption shouldBe putApplicationEnvReqDto.details[0].encryption
                envDetail.applicationEnv shouldBe appEnv
            }
        }
    }
})