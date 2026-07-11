package com.dcd.server.core.domain.application.event.listener

import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.event.DeployApplicationEvent
import com.dcd.server.core.domain.application.model.DeploymentResult
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.RefreshApplicationService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ApplicationEventListener(
    private val refreshApplicationService: RefreshApplicationService,
    private val commandApplicationPort: CommandApplicationPort,
    private val containerPort: ContainerPort,
    private val queryApplicationPort: QueryApplicationPort
) {
    @EventListener
    @Transactional(rollbackFor = [Exception::class])
    fun process(event: ChangeApplicationStatusEvent) {
        val updatedApplication = event.application.copy(
            status = event.status,
            deploymentResult = DeploymentResult.from(event.failureCase, event.failureReasonDetail)
        )

        commandApplicationPort.save(updatedApplication)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun process(event: DeployApplicationEvent) {
        val applicationList = queryApplicationPort.findByIds(event.applicationIdList)

        applicationList.forEach { application ->
            containerPort.execute {
                deleteContainer(application)
                deleteImage(application)
            }

            CoroutineScope(Dispatchers.IO).launch {
                refreshApplicationService.refresh(application)

                val updatedApplication = application.copy(status = ApplicationStatus.STOPPED)
                commandApplicationPort.save(updatedApplication)
            }
        }
    }
}