package com.dcd.server.core.domain.env.service

import com.dcd.server.core.domain.application.model.Application

interface EnvAutoMatchService {
    fun match(application: Application)
}