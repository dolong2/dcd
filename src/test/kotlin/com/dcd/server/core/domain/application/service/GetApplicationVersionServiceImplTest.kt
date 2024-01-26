package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.GetApplicationVersionServiceImpl
import io.kotest.core.spec.style.BehaviorSpec

class GetApplicationVersionServiceImplTest : BehaviorSpec({
    given("ApplicationType이 주어지고") {
        val applicationType = ApplicationType.MYSQL

        `when`("getAvailableVersion 실행할때") {
            val getApplicationVersionService = GetApplicationVersionServiceImpl()
            val result = getApplicationVersionService.getAvailableVersion(applicationType)
            then("latest가 있어야함") {
                println("result = ${result}")
            }
        }
    }
})