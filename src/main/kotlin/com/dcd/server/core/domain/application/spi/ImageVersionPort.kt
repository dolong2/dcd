package com.dcd.server.core.domain.application.spi

interface ImageVersionPort {
    fun fetchAllVersions(imageName: String): List<String>
}