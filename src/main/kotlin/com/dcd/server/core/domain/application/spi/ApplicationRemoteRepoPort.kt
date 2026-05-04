package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application

interface ApplicationRemoteRepoPort {
    fun cloneApplicationRemoteRepo(application: Application)
}