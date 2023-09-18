package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.enums.DBType
import com.dcd.server.core.domain.application.model.Application

interface CreateDockerComposeFileService {
    fun createDockerComposeYml(id: String, dbTypes: Array<DBType>, rootPassword: String, dataBaseName: String)
    fun createDockerComposeYml(application: Application, dbTypes: Array<DBType>, rootPassword: String, dataBaseName: String)
}