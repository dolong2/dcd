package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.volume.spi.VolumeFileStoragePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.UUID

@UseCase
class UploadVolumeFileUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val workspaceInfo: WorkspaceInfo,
    private val fileOperationPort: FileOperationPort,
    private val volumeFileStoragePort: VolumeFileStoragePort
) {
    fun execute(volumeId: UUID, filePath: String, file: MultipartFile) {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()
        val volume = queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException()

        if (workspace != volume.workspace) {
            throw VolumeNotFoundException()
        }

        if (file.isEmpty) {
            return
        }

        val targetPath = resolveVolumePath(volume, filePath)
        try {
            fileOperationPort.writeFileByBytes(targetPath, file.bytes)
        } catch (e: FileOperationException) {
            throw e
        }
    }

    private fun resolveVolumePath(volume: com.dcd.server.core.domain.volume.model.Volume, filePath: String): Path {
        return volumeFileStoragePort.resolveTargetPath(volume, filePath)
    }
}
