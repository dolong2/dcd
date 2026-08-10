package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher

@UseCase
class RunApplicationUseCase(
    private val containerPort: ContainerPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val workspaceInfo: WorkspaceInfo
): CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        launch  {
            try {
                containerPort.execute {
                    startContainer(application)
                }
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.RUNNING, application))
            } catch (e: Exception) {
                log.error("Failed to run application: ${application.name}", e)
            }
        }

        eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, application))
    }

    fun execute(labels: List<String>) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        val runChannel = Channel<Application>(capacity = Channel.UNLIMITED)
        val job = SupervisorJob()
        val scope = this + job
        applicationList.forEach {
            if (it.status == ApplicationStatus.RUNNING)
                return@forEach

            runChannel.trySend(it).isSuccess
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, it))
        }

        // 코루틴을 생성하여 작업 처리
        repeat(3) {
            scope.launch {
                for (application in runChannel) {
                    try {
                        containerPort.execute {
                            startContainer(application)
                        }
                        eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.RUNNING, application))
                    } catch (e: Exception) {
                        log.error("Failed to run application: ${application.name}", e)
                    }
                }
            }
        }

        // 작업 완료 후 코루틴 스코프 종료
        scope.launch {
            runChannel.close()
        }
    }
}