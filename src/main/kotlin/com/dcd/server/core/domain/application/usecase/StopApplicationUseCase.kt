package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.AlreadyStoppedException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.service.StopContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

@UseCase
class StopApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val stopContainerService: StopContainerService,
    private val changeApplicationStatusService: ChangeApplicationStatusService,
    private val workspaceInfo: WorkspaceInfo
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.STOPPED)
            throw AlreadyStoppedException()

        launch {
            stopContainerService.stopContainer(application)
        }

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.PENDING)
    }

    fun execute(labels: List<String>) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        val runChannel = Channel<Application>(capacity = Channel.UNLIMITED)
        val job = SupervisorJob()
        val scope = this + job
        applicationList
            .filter { it.status == ApplicationStatus.RUNNING }
            .forEach {
                runChannel.trySend(it).isSuccess
                changeApplicationStatusService.changeApplicationStatus(it, ApplicationStatus.PENDING)
            }

        // 코루틴을 생성하여 작업 처리
        repeat(3) {
            scope.launch {
                for (application in runChannel) {
                    stopContainerService.stopContainer(application)
                }
            }
        }

        // 작업 완료 후 코루틴 스코프 종료
        scope.launch {
            runChannel.close()
        }
    }
}