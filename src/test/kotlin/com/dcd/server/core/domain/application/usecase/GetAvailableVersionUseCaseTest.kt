package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GetAvailableVersionUseCaseTest : BehaviorSpec({
    val getApplicationVersionService = mockk<GetApplicationVersionService>()

    val getAvailableVersionUseCase = GetAvailableVersionUseCase(getApplicationVersionService)

    given("ApplicationType이 주어지고") {
        val applicationType = ApplicationType.SPRING_BOOT

        `when`("getApplicationVersionService에서 문자열 리스트를 반환할때") {
            val versionList = listOf("17")
            every { getApplicationVersionService.getAvailableVersion(applicationType) } returns versionList

            val result = getAvailableVersionUseCase.execute(applicationType)
            then("반환하는 값은 해당 리스트를 가지고 있어야함") {
                result.version shouldBe versionList
            }
        }
    }
})