package com.dcd.server.core.domain.application.scheduler

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.DeploymentResult
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.application.util.FailureCase
import com.dcd.server.core.common.spi.ContainerPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ApplicationStatusScheduler(
    private val queryApplicationPort: QueryApplicationPort,
    private val containerPort: ContainerPort,
    private val commandApplicationPort: CommandApplicationPort
) {
    /**
     * 각 애플리케이션의 상태를 확인하고, 컨테이너의 상태와 일치하게 수정하는 스케줄러
     * @author dolong2
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = [Exception::class])
    fun checkApplicationStatus() =
        runBlocking {
            val runningApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.RUNNING)
            val stoppedApplicationList = queryApplicationPort.findAllByStatus(ApplicationStatus.STOPPED)

            val checkExitedApplicationList = async(Dispatchers.IO) { checkExitedContainer(runningApplicationList) }
            val checkedRunningApplicationList = async(Dispatchers.IO) { checkRunningContainer(stoppedApplicationList) }
            val checkCreatedContainerApplicationList = async(Dispatchers.IO) { checkCreatedContainer(runningApplicationList) }

            val updatedApplicationList =
                checkExitedApplicationList.await() + checkedRunningApplicationList.await() + checkCreatedContainerApplicationList.await()
            commandApplicationPort.saveAll(updatedApplicationList)
        }

    /**
     * 실행중인 애플리케이션중 컨테이너가 종료된 애플리케이션의 상태가 STOPPED로 변경될 애플리케이션 리스트를 반환하는 메서드
     * @return STOPPED 혹은 FAILURE로 변경될 애플리케이션 리스트
     * @author dolong2
     */
    fun checkExitedContainer(targetApplicationList: List<Application>): List<Application> {
        val updatedApplicationList = mutableListOf<Application>()

        containerPort.execute {
            val exitedContainer = getContainer(ContainerStatus.EXITED)
            exitedContainer.forEach { containerName ->
                val containerExitedApplication = targetApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerExitedApplication.copy(status = ApplicationStatus.STOPPED)
                updatedApplicationList.add(updatedApplication)
            }
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

        containerPort.execute {
            val runningContainer = getContainer(ContainerStatus.RUNNING)
            runningContainer.forEach { containerName ->
                val containerRunningApplication = targetApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach

                val updatedApplication = containerRunningApplication.copy(status = ApplicationStatus.RUNNING)
                updatedApplicationList.add(updatedApplication)
            }
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

        containerPort.execute {
            val createdContainer = getContainer(ContainerStatus.CREATED)
            createdContainer.forEach { containerName ->
                val containerCreatedApplication = targetApplicationList.lastOrNull { it.containerName == containerName }
                    ?: return@forEach
                
                val updatedApplication = containerCreatedApplication.copy(status = ApplicationStatus.STOPPED)
                updatedApplicationList.add(updatedApplication)
            }
        }

        return updatedApplicationList
    }
}