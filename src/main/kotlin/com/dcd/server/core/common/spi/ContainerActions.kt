package com.dcd.server.core.common.spi

import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.volume.model.VolumeMount

interface ContainerActions {
    fun createContainer(application: Application, volumeMounts: List<VolumeMount>): String
    fun startContainer(application: Application): Boolean
    fun stopContainer(application: Application): Boolean
    fun deleteContainer(application: Application): Boolean
    fun deleteImage(application: Application): Boolean
    fun getContainerStatus(application: Application): String
    fun getContainer(status: ContainerStatus): List<String>
    fun getContainerLogs(application: Application): List<String>
    fun buildImage(application: Application, dockerfilePath: String): String
}
