package com.dcd.server.infrastructure.global.adapter

import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Component
class FileOperationAdapter : FileOperationPort {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun createDirectory(path: Path) {
        try {
            Files.createDirectories(path)
        } catch (e: Exception) {
            log.error("Failed to create directory: ${path.toAbsolutePath()}", e)
            throw FileOperationException()
        }
    }

    override fun deleteDirectory(path: Path) {
        try {
            if (!Files.exists(path)) {
                return
            }
            Files.walk(path).use { stream ->
                stream.sorted(Comparator.reverseOrder())
                    .forEach { p -> Files.delete(p)}
            }
        } catch (e: Exception) {
            log.error("Failed to delete directory: ${path.toAbsolutePath()}", e)
            throw FileOperationException()
        }
    }

    override fun writeFile(path: Path, content: String) {
        try {
            val parentDir = path.parent
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir)
            }
            if (Files.isSymbolicLink(path)) {
                throw FileOperationException()
            }
            val options = setOf<OpenOption>(
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE,
                LinkOption.NOFOLLOW_LINKS
            )
            Files.newByteChannel(path, options).use { channel ->
                channel.write(ByteBuffer.wrap(content.toByteArray(StandardCharsets.UTF_8)))
            }
        } catch (e: Exception) {
            log.error("Failed to write file: ${path.toAbsolutePath()}", e)
            throw FileOperationException()
        }
    }

    override fun writeFileByBytes(path: Path, content: ByteArray) {
        try {
            val parentDir = path.parent
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir)
            }
            if (Files.isSymbolicLink(path)) {
                throw FileOperationException()
            }
            val options = setOf<OpenOption>(
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE,
                LinkOption.NOFOLLOW_LINKS
            )
            Files.newByteChannel(path, options).use { channel ->
                channel.write(ByteBuffer.wrap(content))
            }
        } catch (e: Exception) {
            log.error("Failed to write file: ${path.toAbsolutePath()}", e)
            throw FileOperationException()
        }
    }

    override fun deleteFile(path: Path) {
        try {
            if (Files.exists(path)) {
                Files.delete(path)
            }
        } catch (e: Exception) {
            log.error("Failed to delete file: ${path.toAbsolutePath()}", e)
            throw FileOperationException()
        }
    }
}
