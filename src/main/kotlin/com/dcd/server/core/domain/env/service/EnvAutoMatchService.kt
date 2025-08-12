package com.dcd.server.core.domain.env.service

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.workspace.model.Workspace

interface EnvAutoMatchService {
    fun match(workspace: Workspace, application: Application)
}