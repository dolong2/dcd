package com.dcd.server.infrastructure.domain.volume.adapter

import com.dcd.server.core.domain.volume.exception.InvalidVolumeFilePathException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.spi.VolumeFileStoragePort
import com.github.dockerjava.api.DockerClient
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

@Component
class DockerVolumeFileStorageAdapter(
    private val dockerClient: DockerClient
) : VolumeFileStoragePort {
    override fun resolveVolumeRootPath(volume: Volume): Path {
        val volumeInfo = dockerClient.inspectVolumeCmd(volume.volumeName).exec()
        val mountPoint = volumeInfo.mountpoint ?: throw IllegalStateException("Volume mountpoint is null: ${volume.volumeName}")
        return Paths.get(mountPoint).toAbsolutePath().normalize()
    }

    override fun resolveTargetPath(volume: Volume, relativePath: String): Path {
        val rootPath = resolveVolumeRootPath(volume)
        val normalizedPath = relativePath.trim().removePrefix("/")

        if (normalizedPath.isBlank()) {
            return rootPath
        }

        val segments = normalizedPath.split('/')
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .filter { it != "." && it != ".." }

        if (segments.isEmpty() || segments.any { it == ".." || it == "." || it.isBlank() }) {
            throw InvalidVolumeFilePathException()
        }

        var current = rootPath
        for (segment in segments) {
            current = current.resolve(segment).normalize()
            if (!current.startsWith(rootPath)) {
                throw InvalidVolumeFilePathException()
            }
            if (Files.isSymbolicLink(current) || Files.exists(current, LinkOption.NOFOLLOW_LINKS) == false && Files.isSymbolicLink(current)) {
                throw InvalidVolumeFilePathException()
            }
        }

        val finalPath = rootPath.resolve(segments.joinToString("/")).normalize()
        if (!finalPath.startsWith(rootPath)) {
            throw InvalidVolumeFilePathException()
        }
        if (Files.isSymbolicLink(finalPath) || Files.exists(finalPath, LinkOption.NOFOLLOW_LINKS) == false && Files.isSymbolicLink(finalPath)) {
            throw InvalidVolumeFilePathException()
        }

        return finalPath
    }
}
