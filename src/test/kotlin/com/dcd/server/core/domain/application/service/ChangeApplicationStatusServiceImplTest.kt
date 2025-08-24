package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.impl.ChangeApplicationStatusServiceImpl
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import util.application.ApplicationGenerator

class ChangeApplicationStatusServiceImplTest : BehaviorSpec({
    val commandApplicationPort = mockk<CommandApplicationPort>(relaxUnitFun = true)
    val changeApplicationStatusService = ChangeApplicationStatusServiceImpl(commandApplicationPort)

    given("실행중인 애플리케이션이 주어지고") {
        val application = spyk(ApplicationGenerator.generateApplication(status = ApplicationStatus.RUNNING))

        `when`("ApplicationStatus.STOPPED를 대입할때") {
            val targetApplication = application.copy(status = ApplicationStatus.STOPPED)
            every { application.copy(status = ApplicationStatus.STOPPED) } returns targetApplication
            changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.STOPPED)

            then("애플리케이션의 상태를 STOPPED로 업데이트 해야함") {
                application.status shouldBe ApplicationStatus.RUNNING
                verify { commandApplicationPort.save(targetApplication) }
            }
        }
    }

    given("정지된 애플리케이션이 주어지고") {
        val application = spyk(ApplicationGenerator.generateApplication())

        `when`("ApplicationStatus.RUNNING을 대입할때") {
            val targetApplication = application.copy(status = ApplicationStatus.RUNNING)
            every { application.copy(status = ApplicationStatus.RUNNING) } returns targetApplication
            changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING)

            then("애플리케이션의 상태를 STOPPED로 업데이트 해야함") {
                application.status shouldBe ApplicationStatus.STOPPED
                verify { commandApplicationPort.save(targetApplication) }
            }
        }
    }
})