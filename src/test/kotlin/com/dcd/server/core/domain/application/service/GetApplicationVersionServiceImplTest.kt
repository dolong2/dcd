package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
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
            every { commandPort.executeShellCommandWithResult("docker images ${applicationType.name.lowercase()}") } returns
                    listOf(
                        "REPOSITORY   TAG       IMAGE ID       CREATED         SIZE\n",
                        "mysql        <none>    2010e93e56ca   8 weeks ago     608MB\n",
                        "mysql        <none>    affb2bfc837e   8 weeks ago     608MB\n",
                        "mysql        latest    0367e9abf33f   8 weeks ago     609MB\n",
                        "mysql        <none>    f958d2869db0   8 weeks ago     609MB\n",
                        "mysql        <none>    70e413dee93d   2 months ago    608MB\n",
                        "mysql        <none>    c3239d2b6d88   2 months ago    608MB"
                    )

            val getApplicationVersionService = GetApplicationVersionServiceImpl(commandPort)
            val result = getApplicationVersionService.getAvailableVersion(applicationType)
            then("latest가 있어야함") {
                result.size shouldBe 6
                result.contains("latest") shouldBe true
            }
        }
    }
})