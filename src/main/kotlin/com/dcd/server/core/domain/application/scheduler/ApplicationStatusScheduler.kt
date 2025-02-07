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
    /**
     * 실행중인 애플리케이션중 컨테이너가 종료된 애플리케이션의 상태를 STOPPED로 변경하는 스케줄러
     * @author dolong2
     */
    @Scheduled(cron = "0 * * * * ?")
    fun checkExitedApplication() {
        val runningApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.RUNNING)

        val updatedApplicationList = mutableListOf<Application>()
        getContainerService.getContainerNameByStatus(ContainerStatus.EXITED)
            .forEach { result ->
                val (containerName, exitCode) = result.split(" ")
                val containerExitedApplication = runningApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication =
                    if (exitCode == "0")
                        containerExitedApplication.copy(status = ApplicationStatus.STOPPED)
                    else
                        containerExitedApplication.copy(status = ApplicationStatus.FAILURE, failureReason = "컨테이너가 비정상적으로 종료됨")

                updatedApplicationList.add(updatedApplication)
            }

        commandApplicationPort.saveAll(updatedApplicationList)
    }

    /**
     * 정지된 애플리케이션중 실행중인 컨테이너가 있는 애플리케이션의 상태를 RUNNING으로 변경하는 스케줄러
     * @author dolong2
     */
    @Scheduled(cron = "0 * * * * ?")
    fun checkRunningApplication() {
        val stoppedApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.STOPPED)

        val updatedApplicationList = mutableListOf<Application>()
        getContainerService.getContainerNameByStatus(ContainerStatus.RUNNING)
            .forEach { result ->
                val (containerName, _) = result.split(" ")
                val containerRunningApplication = stoppedApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerRunningApplication.copy(
                    status = ApplicationStatus.RUNNING
                )
                updatedApplicationList.add(updatedApplication)
            }

        commandApplicationPort.saveAll(updatedApplicationList)
    }

    /**
     * 실행중인 상태인 애플리케이션중 컨테이너가 생성된 상태가 있는 애플리케이션이 있다면 정지됨 상태로 변경하는 스케줄러
     * @author dolong2
     */
    @Scheduled(cron = "0 * * * * ?")
    fun checkCreatedContainer() {
        val runningApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.RUNNING)

        val updatedApplicationList = mutableListOf<Application>()
        getContainerService.getContainerNameByStatus(ContainerStatus.CREATED)
            .forEach { result ->
                val (containerName, _) = result.split(" ")
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