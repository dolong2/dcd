package com.dcd.server.core.common.file.spi

import java.nio.file.Path

interface FileOperationPort {
    fun createDirectory(path: Path)

    fun deleteDirectory(path: Path)

    fun writeFile(path: Path, content: String)

    fun deleteFile(path: Path)
}
