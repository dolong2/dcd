package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application

interface ApplicationImageFilePort {
    suspend fun createImageFile(application: Application)
}
