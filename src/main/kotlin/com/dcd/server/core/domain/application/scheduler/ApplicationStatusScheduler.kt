package com.dcd.server.core.domain.application.scheduler

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.service.GetContainerService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ApplicationStatusScheduler(
    private val queryApplicationPort: QueryApplicationPort,
    private val getContainerService: GetContainerService,
    private val commandApplicationPort: CommandApplicationPort
) {
    @Scheduled(cron = "0 * * * * ?")
    fun checkExitedApplication() {
        val runningApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.RUNNING)

        val updatedApplicationList = mutableListOf<Application>()
        getContainerService.getContainerNameByStatus(ContainerStatus.EXITED)
            .forEach {containerName ->
                val containerExitedApplication = runningApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerExitedApplication.copy(
                    status = ApplicationStatus.STOPPED
                )
                updatedApplicationList.add(updatedApplication)
            }

        commandApplicationPort.saveAll(updatedApplicationList)
    }

    @Scheduled(cron = "0 * * * * ?")
    fun checkRunningApplication() {
        val stoppedApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.STOPPED)

        val updatedApplicationList = mutableListOf<Application>()
        getContainerService.getContainerNameByStatus(ContainerStatus.RUNNING)
            .forEach {containerName ->
                val containerRunningApplication = stoppedApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerRunningApplication.copy(
                    status = ApplicationStatus.RUNNING
                )
                updatedApplicationList.add(updatedApplication)
            }

        commandApplicationPort.saveAll(updatedApplicationList)
    }

    @Scheduled(cron = "0 * * * * ?")
    fun checkCreatedContainer() {
        val runningApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.RUNNING)

        val updatedApplicationList = mutableListOf<Application>()
        getContainerService.getContainerNameByStatus(ContainerStatus.CREATED)
            .forEach {containerName ->
                val containerExitedApplication = runningApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerExitedApplication.copy(
                    status = ApplicationStatus.STOPPED
                )
                updatedApplicationList.add(updatedApplication)
            }

        commandApplicationPort.saveAll(updatedApplicationList)
    }
}