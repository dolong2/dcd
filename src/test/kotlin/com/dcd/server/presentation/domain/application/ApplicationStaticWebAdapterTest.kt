package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.dto.response.AvailableVersionResDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.GetAvailableVersionUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus

class ApplicationStaticWebAdapterTest : BehaviorSpec({
    val getAvailableVersionUseCase = mockk<GetAvailableVersionUseCase>()
    val applicationWebAdapter = ApplicationStaticWebAdapter(getAvailableVersionUseCase)

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
})