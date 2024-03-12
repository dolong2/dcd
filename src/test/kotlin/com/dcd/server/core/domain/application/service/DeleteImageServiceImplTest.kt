package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.impl.DeleteImageServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator

class DeleteImageServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val deleteImageService = DeleteImageServiceImpl(commandPort)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("서비스를 실행할때") {
            deleteImageService.deleteImage(application)

            then("이미지를 제거하는 명령이 실행되어야함") {
                verify { commandPort.executeShellCommand("docker rmi ${application.name.lowercase()}") }
            }
        }
    }
})