package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PutApplicationEnvUseCaseTest(
    private val putApplicationEnvUseCase: PutApplicationEnvUseCase,
    private val queryApplicationPort: QueryApplicationPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort
) : BehaviorSpec({
    val targetApplicationId = "2fb0f315-8272-422f-8e9f-c4f765c022b2"

    given("애플리케이션 아이디와 request가 주어지고") {
        val request = PutApplicationEnvReqDto(
            envList = listOf(PutEnvReqDto("testA", "testB", false))
        )
        `when`("usecase를 실행할때") {
            putApplicationEnvUseCase.execute(targetApplicationId, request)
            then("타겟 애플리케이션에 환경변수가 추가되어야함") {
                val result = queryApplicationPort.findById(targetApplicationId)
                result shouldNotBe null

                val appEnv = queryApplicationEnvPort.findByKeyAndApplication("testA", result!!)
                appEnv?.value shouldBe "testB"
            }
        }
    }

    given("존재하지 않는 애플리케이션 아이디가 주어지고") {
        val notFoundApplicationId = UUID.randomUUID().toString()
        val request = PutApplicationEnvReqDto(
            envList = listOf(PutEnvReqDto("testKey", "testValue", false))
        )

        `when`("usecase를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    putApplicationEnvUseCase.execute(notFoundApplicationId, request)
                }
            }
        }
    }
})