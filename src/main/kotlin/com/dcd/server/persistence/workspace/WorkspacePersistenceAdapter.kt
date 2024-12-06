package com.dcd.server.persistence.workspace

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.WorkspacePort
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.workspace.adapter.toDomain
import com.dcd.server.persistence.workspace.adapter.toEntity
import com.dcd.server.persistence.workspace.repository.WorkspaceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class WorkspacePersistenceAdapter(
    private val workspaceRepository: WorkspaceRepository
) : WorkspacePort {
    override fun findById(id: String): Workspace? {
        return workspaceRepository
            .findByIdOrNull(id)
            ?.toDomain()
    }

    override fun findAll(): List<Workspace> {
        return workspaceRepository.findAll().map { it.toDomain() }
    }

    override fun findByUser(user: User): List<Workspace> {
        return workspaceRepository.findAllByOwner(user.toEntity()).map { it.toDomain() }
    }

    override fun existsByTitle(title: String): Boolean =
        workspaceRepository.existsByTitle(title)

    override fun save(workspace: Workspace) {
        workspaceRepository.save(workspace.toEntity())
    }

    override fun delete(workspace: Workspace) {
        workspaceRepository.delete(workspace.toEntity())
    }
}