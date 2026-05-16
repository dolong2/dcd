package com.dcd.server.core.common.spi

import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.model.Workspace

interface ContainerActions {
    fun createContainer(application: Application, volumeMounts: List<VolumeMount>)
    fun startContainer(application: Application)
    fun stopContainer(application: Application)
    fun deleteContainer(application: Application)
    fun deleteImage(application: Application)
    fun getContainer(status: ContainerStatus): List<String>
    fun getContainerLogs(application: Application): List<String>
    fun buildImage(application: Application, dockerfilePath: String)
    fun executeCmd(application: Application, workingDir: String, cmd: String, onResponse: (String) -> Unit)
    fun executeCmd(containerName: String, cmd: String)

    fun createVolume(volume: Volume)
    fun deleteVolume(volume: Volume)
    fun copyVolume(sourceVolume: Volume, targetVolume: Volume)

    fun createNetwork(workspace: Workspace)
    fun deleteNetwork(workspace: Workspace)
    fun connectNetwork(workspace: Workspace, application: Application)
    fun disconnectNetwork(workspace: Workspace, application: Application)
}
