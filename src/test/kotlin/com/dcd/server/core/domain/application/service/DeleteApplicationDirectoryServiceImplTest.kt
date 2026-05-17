package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.application.service.impl.DeleteApplicationDirectoryServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher
import util.application.ApplicationGenerator
import java.nio.file.Paths

class DeleteApplicationDirectoryServiceImplTest : BehaviorSpec({
    val fileOperationPort = mockk<FileOperationPort>(relaxUnitFun = true)
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxUnitFun = true)
    val service = DeleteApplicationDirectoryServiceImpl(fileOperationPort, eventPublisher)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("deleteApplicationDirectory 메서드를 실행할때") {
            service.deleteApplicationDirectory(application)

            then("fileOperationPort.deleteDirectory가 실행되어야함") {
                verify { fileOperationPort.deleteDirectory(Paths.get(application.name)) }
            }
        }
    }
})
