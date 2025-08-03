package com.dcd.server.core.domain.env.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail

interface CommandApplicationEnvPort {
    fun save(applicationEnv: ApplicationEnv, application: Application)

    fun saveAll(applicationEnvList: List<ApplicationEnv>, application: Application)

    fun delete(applicationEnv: ApplicationEnv)

    fun deleteAll(applicationEnvList: List<ApplicationEnv>)

    fun deleteDetail(applicationEnvDetail: ApplicationEnvDetail)
}