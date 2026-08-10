package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.ApplicationImageFilePort
import com.dcd.server.core.domain.application.spi.ApplicationRemoteRepoPort
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.RefreshApplicationService
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import org.springframework.stereotype.Service

@Service
class RefreshApplicationServiceImpl(
    private val containerPort: ContainerPort,
    private val applicationRemoteRepoPort: ApplicationRemoteRepoPort,
    private val applicationImageFilePort: ApplicationImageFilePort,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val queryVolumePort: QueryVolumePort
) : RefreshApplicationService {
    @Lock("#application.id", waitTime = 1000 * 10, leaseTime = 1000 * 60 * 3)
    override suspend fun refresh(application: Application) {
        val applicationType = application.applicationType
        when(applicationType) {
            ApplicationType.SPRING_BOOT, ApplicationType.NEST_JS, ApplicationType.GIN -> {
                applicationRemoteRepoPort.cloneApplicationRemoteRepo(application)
            }
            else -> {}
        }

        applicationImageFilePort.createImageFile(application)

        containerPort.execute {
            buildImage(application)
            val volumeMounts = queryVolumePort.findAllMountByApplication(application)
            try {
                createContainer(application, volumeMounts)
            } catch (e: Exception) {
                runCatching { deleteImage(application) }
                throw e
            }
        }

        deleteApplicationDirectoryService.deleteApplicationDirectory(application)
    }
}