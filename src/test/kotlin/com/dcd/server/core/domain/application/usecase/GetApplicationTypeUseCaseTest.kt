package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class GetApplicationTypeUseCaseTest : BehaviorSpec({

    given("애플리케이션 타입 조회 유스케이스가 주어지고") {
        val getApplicationTypeUseCase = GetApplicationTypeUseCase()

        `when`("유스케이스를 실행할때") {
            val result = getApplicationTypeUseCase.execute()

            then("현재 지원가능한 애플리케이션의 타입이 조회되어야함") {
                result.list shouldBe ApplicationType.values().map { it.name }
            }
        }
    }
})