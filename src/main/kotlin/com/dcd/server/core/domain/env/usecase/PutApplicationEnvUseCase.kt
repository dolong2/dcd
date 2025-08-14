package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.domain.application.event.DeployApplicationEvent
import com.dcd.server.core.domain.env.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.dto.extension.toModel
import com.dcd.server.core.domain.env.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.env.model.ApplicationEnvMatcher
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@UseCase
class PutApplicationEnvUseCase(
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val encryptService: EncryptService,
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun execute(putApplicationEnvReqDto: PutApplicationEnvReqDto) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationEnv = putApplicationEnvReqDto.toModel(workspace, encryptService)
        commandApplicationEnvPort.save(applicationEnv)

        val applicationListByLabel = putApplicationEnvReqDto.applicationLabelList
            ?.let { queryApplicationPort.findAllByWorkspace(workspace, it) }
            ?: emptyList()
        val applicationListByIds = (putApplicationEnvReqDto.applicationIdList
            ?.let { queryApplicationPort.findByIds(it) }
            ?: emptyList())

        val applicationSet = (applicationListByLabel + applicationListByIds).toSet()

        val envMatcherList = applicationSet.map {
            ApplicationEnvMatcher(
                application = it,
                applicationEnv = applicationEnv
            )
        }
        commandApplicationEnvPort.saveAllMatcher(envMatcherList)

        if (applicationSet.isNotEmpty()) {
            eventPublisher.publishEvent(DeployApplicationEvent(applicationSet.map { it.id }))
        }
    }

    fun execute(id: UUID, putApplicationEnvReqDto: PutApplicationEnvReqDto) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationEnv = (queryApplicationEnvPort.findById(id)
            ?: throw ApplicationEnvNotFoundException())

        if (workspace != applicationEnv.workspace)
            throw ApplicationEnvNotFoundException()

        commandApplicationEnvPort.deleteAllDetail(applicationEnv)
        val envDetail = putApplicationEnvReqDto.details.map { it.toModel(encryptService) }

        val updatedEnv = applicationEnv.copy(
            name = putApplicationEnvReqDto.name,
            description = putApplicationEnvReqDto.description,
            details = envDetail
        )
        commandApplicationEnvPort.save(updatedEnv)

        commandApplicationEnvPort.deleteAllMatcherByEnv(applicationEnv)
        val applicationListByLabel = putApplicationEnvReqDto.applicationLabelList
            ?.let { queryApplicationPort.findAllByWorkspace(workspace, it) }
            ?: emptyList()
        val applicationListByIds = (putApplicationEnvReqDto.applicationIdList
            ?.let { queryApplicationPort.findByIds(it) }
            ?: emptyList())

        val applicationSet = (applicationListByLabel + applicationListByIds).toSet()

        val envMatcherList = applicationSet.map {
            ApplicationEnvMatcher(
                application = it,
                applicationEnv = updatedEnv
            )
        }
        commandApplicationEnvPort.saveAllMatcher(envMatcherList)

        if (applicationSet.isNotEmpty()) {
            eventPublisher.publishEvent(DeployApplicationEvent(applicationSet.map { it.id }))
        }
    }
}