package com.dcd.server.infrastructure.domain.volume.adapter

import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.spi.VolumeFileStoragePort
import com.github.dockerjava.api.DockerClient
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.Paths

@Component
class DockerVolumeFileStorageAdapter(
    private val dockerClient: DockerClient
) : VolumeFileStoragePort {
    override fun resolveVolumeRootPath(volume: Volume): Path {
        val volumeInfo = dockerClient.inspectVolumeCmd(volume.volumeName).exec()
        val mountPoint = volumeInfo.mountpoint ?: throw IllegalStateException("Volume mountpoint is null: ${volume.volumeName}")
        return Paths.get(mountPoint)
    }

    override fun resolveTargetPath(volume: Volume, relativePath: String): Path {
        val normalizedPath = relativePath.trim().removePrefix("/")
        val rootPath = resolveVolumeRootPath(volume)

        return if (normalizedPath.isBlank()) {
            rootPath
        } else {
            val sanitized = normalizedPath.split("/")
                .filter { it.isNotBlank() }
                .filter { it != "." && it != ".." }
                .joinToString("/")

            rootPath.resolve(sanitized)
        }
    }
}
