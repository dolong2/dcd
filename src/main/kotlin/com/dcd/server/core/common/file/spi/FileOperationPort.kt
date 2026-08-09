package com.dcd.server.core.common.file.spi

import java.nio.file.Path

interface FileOperationPort {
    fun createDirectory(path: Path)

    fun deleteDirectory(path: Path)

    fun writeFile(path: Path, content: String)

    fun writeFileByBytes(path: Path, content: ByteArray)

    fun deleteFile(path: Path)
}
