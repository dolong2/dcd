package com.dcd.server.core.common.spi

import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.volume.model.VolumeMount

interface ContainerActions {
    fun createContainer(application: Application, volumeMounts: List<VolumeMount>)
    fun startContainer(application: Application)
    fun stopContainer(application: Application)
    fun deleteContainer(application: Application)
    fun deleteImage(application: Application)
    fun getContainer(status: ContainerStatus): List<String>
    fun getContainerLogs(application: Application): List<String>
    fun buildImage(application: Application, dockerfilePath: String)
}
