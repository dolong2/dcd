package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application

interface ApplicationImageFilePort {
    fun createImageFile(application: Application)
}
