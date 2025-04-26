package com.dcd.server.persistence.env

import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.spi.GlobalEnvPort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.env.adapter.toDomain
import com.dcd.server.persistence.env.adapter.toEntity
import com.dcd.server.persistence.env.repository.GlobalEnvRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class GlobalEnvPersistenceAdapter(
    private val globalEnvRepository: GlobalEnvRepository
) : GlobalEnvPort {
    override fun findById(id: UUID): GlobalEnv? =
        globalEnvRepository.findByIdOrNull(id)?.toDomain()

    override fun findByKeyAndWorkspace(key: String, workspace: Workspace): GlobalEnv? =
        globalEnvRepository.findByWorkspaceAndKey(workspace.toEntity(), key)?.toDomain()

    override fun save(globalEnv: GlobalEnv, workspace: Workspace) {
        val globalEnvEntity = globalEnv.toEntity(workspace)
        globalEnvRepository.save(globalEnvEntity)
    }

    override fun saveAll(globalEnvList: List<GlobalEnv>, workspace: Workspace) {
        val globalEnvEntityList = globalEnvList.map { it.toEntity(workspace) }
        globalEnvRepository.saveAll(globalEnvEntityList)
    }

    override fun delete(globalEnv: GlobalEnv) {
        globalEnvRepository.deleteById(globalEnv.id)
    }

    override fun deleteAll(globalEnvList: List<GlobalEnv>) {
        globalEnvRepository.deleteAllById(globalEnvList.map { it.id })
    }
}