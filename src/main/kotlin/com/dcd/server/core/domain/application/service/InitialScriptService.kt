package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface InitialScriptService {
    fun write(application: Application, initialScripts: List<String>)
}