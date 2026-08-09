package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.volume.exception.InvalidVolumeFilePathException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.exception.VolumeUploadFailureException
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.volume.spi.VolumeFileStoragePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@UseCase
class UploadVolumeFileUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val workspaceInfo: WorkspaceInfo,
    private val fileOperationPort: FileOperationPort,
    private val volumeFileStoragePort: VolumeFileStoragePort
) {
    fun execute(volumeId: UUID, filePath: String, file: MultipartFile, createDirectory: Boolean) {
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

        if (targetPath.fileName.toString().isBlank()) {
            throw InvalidVolumeFilePathException()
        }

        val parentDir = targetPath.parent ?: throw InvalidVolumeFilePathException()
        when {
            createDirectory && !Files.exists(parentDir) -> fileOperationPort.createDirectory(parentDir)
            createDirectory -> Unit
            !createDirectory && !Files.exists(parentDir) -> throw InvalidVolumeFilePathException()
            else -> Unit
        }

        try {
            fileOperationPort.writeFileByBytes(targetPath, file.bytes)
        } catch (e: FileOperationException) {
            throw VolumeUploadFailureException()
        }
    }

    private fun resolveVolumePath(volume: com.dcd.server.core.domain.volume.model.Volume, filePath: String): Path {
        val normalizedPath = filePath.trim()
        val invalid = normalizedPath.contains("..") || normalizedPath.contains("\\")
        if (normalizedPath.isBlank() || invalid) {
            throw InvalidVolumeFilePathException()
        }
        return volumeFileStoragePort.resolveTargetPath(volume, normalizedPath)
    }
}
