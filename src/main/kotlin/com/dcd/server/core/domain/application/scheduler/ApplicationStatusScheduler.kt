package com.dcd.server.core.domain.application.scheduler

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.service.GetContainerService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ApplicationStatusScheduler(
    private val queryApplicationPort: QueryApplicationPort,
    private val getContainerService: GetContainerService,
    private val commandApplicationPort: CommandApplicationPort
) {
    /**
     * 각 애플리케이션의 상태를 확인하고, 컨테이너의 상태와 일치하게 수정하는 스케줄러
     * @author dolong2
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = [Exception::class])
    fun checkApplicationStatus() {
        val runningApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.RUNNING)
        val stoppedApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.STOPPED)

        val checkExitedApplicationList = checkExitedContainer(runningApplicationList)
        val checkedRunningApplicationList = checkRunningContainer(stoppedApplicationList)
        val checkCreatedContainerApplicationList = checkCreatedContainer(runningApplicationList)

        val updatedApplicationList =
            checkExitedApplicationList + checkedRunningApplicationList + checkCreatedContainerApplicationList
        commandApplicationPort.saveAll(updatedApplicationList)
    }

    /**
     * 실행중인 애플리케이션중 컨테이너가 종료된 애플리케이션의 상태가 STOPPED로 변경될 애플리케이션 리스트를 반환하는 메서드
     * @return STOPPED 혹은 FAILURE로 변경될 애플리케이션 리스트
     * @author dolong2
     */
    fun checkExitedContainer(targetApplicationList: List<Application>): List<Application> {
        val updatedApplicationList = mutableListOf<Application>()

        getContainerService.getContainerNameByStatus(ContainerStatus.EXITED)
            .forEach { result ->
                val (containerName, exitCode) = result.split(" ")
                val containerExitedApplication = targetApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication =
                    if (exitCode == "0")
                        containerExitedApplication.copy(status = ApplicationStatus.STOPPED)
                    else
                        containerExitedApplication.copy(status = ApplicationStatus.FAILURE, failureReason = "컨테이너가 비정상적으로 종료됨")

                updatedApplicationList.add(updatedApplication)
            }

        return updatedApplicationList
    }

    /**
     * 정지된 애플리케이션중 실행중인 컨테이너가 있는 애플리케이션의 상태가 RUNNING으로 변경될 애플리케이션 리스트를 반환하는 메서드
     * @return RUNNING으로 변경될 애플리케이션 리스트
     * @author dolong2
     */
    fun checkRunningContainer(targetApplicationList: List<Application>): List<Application> {
        val updatedApplicationList = mutableListOf<Application>()

        getContainerService.getContainerNameByStatus(ContainerStatus.RUNNING)
            .forEach { result ->
                val (containerName, _) = result.split(" ")
                val containerRunningApplication = targetApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerRunningApplication.copy(
                    status = ApplicationStatus.RUNNING
                )
                updatedApplicationList.add(updatedApplication)
            }

        return updatedApplicationList
    }

    /**
     * 실행중인 상태인 애플리케이션중 컨테이너가 생성된 상태가 있는 애플리케이션이 있다면 STOPPED로 변경될 애플리케이션 리스트를 반환하는 메서드
     * @return STOPPED로 변경될 애플리케이션 리스트
     * @author dolong2
     */
    fun checkCreatedContainer(targetApplicationList: List<Application>): List<Application> {
        val updatedApplicationList = mutableListOf<Application>()

        getContainerService.getContainerNameByStatus(ContainerStatus.CREATED)
            .forEach { result ->
                val (containerName, _) = result.split(" ")
                val containerExitedApplication = updatedApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerExitedApplication.copy(
                    status = ApplicationStatus.STOPPED
                )
                updatedApplicationList.add(updatedApplication)
            }

        return updatedApplicationList
    }
}