package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.GetApplicationVersionServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetApplicationVersionServiceImplTest : BehaviorSpec({
    given("ApplicationType이 주어지고") {
        val applicationType = ApplicationType.MYSQL
        val commandPort = mockk<CommandPort>()

        `when`("getAvailableVersion 실행할때") {
            val imageVersionShellScriptContent = FileContent.getImageVersionShellScriptContent("mysql", "8")
            val tagList = listOf(
                "latest",
                "11.1.0",
                "10.0.0",
                "9.0.0",
                "8.1.0",
                "8.0.1"
            )
            every { commandPort.executeShellCommandWithResult(imageVersionShellScriptContent) } returns tagList

            val getApplicationVersionService = GetApplicationVersionServiceImpl(commandPort)
            val result = getApplicationVersionService.getAvailableVersion(applicationType)
            then("latest가 있어야함") {
                result.size shouldBe 6
                result shouldBe tagList
            }
        }
    }
})