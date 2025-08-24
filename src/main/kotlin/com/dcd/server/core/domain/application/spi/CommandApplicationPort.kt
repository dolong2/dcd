package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application

interface CommandApplicationPort {
    fun save(application: Application)
    fun delete(application: Application)
    fun saveAll(applicationList: List<Application>)
}