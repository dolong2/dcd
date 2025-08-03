package com.dcd.server.persistence.env

import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.model.GlobalEnvDetail
import com.dcd.server.core.domain.env.spi.GlobalEnvPort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.env.adapter.toDomain
import com.dcd.server.persistence.env.adapter.toEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvDetailEntity
import com.dcd.server.persistence.env.entity.GlobalEnvDetailEntity
import com.dcd.server.persistence.env.repository.GlobalEnvDetailsRepository
import com.dcd.server.persistence.env.repository.GlobalEnvRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class GlobalEnvPersistenceAdapter(
    private val globalEnvRepository: GlobalEnvRepository,
    private val globalEnvDetailsRepository: GlobalEnvDetailsRepository
) : GlobalEnvPort {
    override fun findById(id: UUID): GlobalEnv? =
        globalEnvRepository.findByIdOrNull(id)?.toDomain()

    override fun findByKeyAndWorkspace(key: String, workspace: Workspace): GlobalEnvDetail? =
        globalEnvDetailsRepository.findByWorkspaceAndKey(workspace.toEntity(), key)?.toDomain()

    override fun save(globalEnv: GlobalEnv, workspace: Workspace) {
        val workspaceJpaEntity = workspace.toEntity()
        val globalEnvEntity = globalEnv.toEntity(workspaceJpaEntity)
        globalEnvRepository.save(globalEnvEntity)

        globalEnvDetailsRepository.saveAll(globalEnv.details.map { it.toEntity(globalEnvEntity) })
    }

    override fun saveAll(globalEnvList: List<GlobalEnv>, workspace: Workspace) {
        val workspaceJpaEntity = workspace.toEntity()

        val globalEnvDetailList = mutableListOf<GlobalEnvDetailEntity>()

        val globalEnvEntityList = globalEnvList.map {
            val globalEnvEntity = it.toEntity(workspaceJpaEntity)

            val detailList = it.details.map { envDetail -> envDetail.toEntity(globalEnvEntity) }
            globalEnvDetailList.addAll(detailList)

            globalEnvEntity
        }
        globalEnvRepository.saveAll(globalEnvEntityList)

        globalEnvDetailsRepository.saveAll(globalEnvDetailList)
    }

    override fun delete(globalEnv: GlobalEnv) {
        globalEnvRepository.deleteById(globalEnv.id)
    }

    override fun deleteAll(globalEnvList: List<GlobalEnv>) {
        globalEnvRepository.deleteAllById(globalEnvList.map { it.id })
    }

    override fun deleteDetail(globalEnvDetail: GlobalEnvDetail) {
        globalEnvDetailsRepository.deleteById(globalEnvDetail.id)
    }
}