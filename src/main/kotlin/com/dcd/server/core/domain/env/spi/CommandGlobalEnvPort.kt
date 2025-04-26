package com.dcd.server.core.domain.env.spi

import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.workspace.model.Workspace

interface CommandGlobalEnvPort {
    fun save(globalEnv: GlobalEnv, workspace: Workspace)

    fun saveAll(globalEnvList: List<GlobalEnv>, workspace: Workspace)

    fun delete(globalEnv: GlobalEnv)

    fun deleteAll(globalEnvList: List<GlobalEnv>)
}