package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.volume.exception.InvalidVolumeFilePathException
import com.dcd.server.core.domain.volume.exception.VolumeFileDeleteFailureException
import com.dcd.server.core.domain.volume.exception.VolumeFileNotFoundException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.volume.spi.VolumeFileStoragePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@UseCase
class DeleteVolumeFileUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val workspaceInfo: WorkspaceInfo,
    private val fileOperationPort: FileOperationPort,
    private val volumeFileStoragePort: VolumeFileStoragePort
) {
    fun execute(volumeId: UUID, filePath: String) {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()
        val volume = queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException()

        if (workspace != volume.workspace) {
            throw VolumeNotFoundException()
        }

        val targetPath = resolveVolumePath(volume, filePath)

        if (!Files.exists(targetPath)) {
            throw VolumeFileNotFoundException()
        }

        try {
            fileOperationPort.deleteFile(targetPath)
        } catch (e: FileOperationException) {
            throw VolumeFileDeleteFailureException()
        }
    }

    private fun resolveVolumePath(volume: Volume, filePath: String): Path {
        val normalizedPath = filePath.trim()
        val invalid = normalizedPath.contains("..") || normalizedPath.contains("\\")
        if (normalizedPath.isBlank() || invalid) {
            throw InvalidVolumeFilePathException()
        }
        return volumeFileStoragePort.resolveTargetPath(volume, normalizedPath)
    }
}
