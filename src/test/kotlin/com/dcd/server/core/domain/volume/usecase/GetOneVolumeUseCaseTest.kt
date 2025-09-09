package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toDetailResDto
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.volume.adapter.toDomain
import com.dcd.server.persistence.volume.adapter.toEntity
import com.dcd.server.persistence.volume.repository.VolumeRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import com.dcd.server.persistence.workspace.repository.WorkspaceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.workspace.WorkspaceGenerator
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetOneVolumeUseCaseTest(
    private val getOneVolumeUseCase: GetOneVolumeUseCase,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val workspaceRepository: WorkspaceRepository,
    private val volumeRepository: VolumeRepository,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val targetVolumeId = UUID.randomUUID()

    beforeSpec {
        val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        val volume = Volume(
            id = targetVolumeId,
            name = "test1Volume",
            description = "testDescription",
            workspace = targetWorkspace
        ).toEntity()
        volumeRepository.save(volume)
    }

    given("알맞는 워크스페이스가 세팅되고") {
        beforeContainer {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        `when`("유스케이스를 실행할때") {
            val result = getOneVolumeUseCase.execute(targetVolumeId)

            then("타켓 볼륨의 정보가 반환되어야함") {
                val targetVolume = volumeRepository.findByIdOrNull(targetVolumeId)
                targetVolume.shouldNotBeNull()

                result shouldBe targetVolume.toDomain().toDetailResDto(listOf())
            }
        }

        `when`("존재하지 않는 볼륨아이디로 실행할때") {
            val invalidVolumeId = UUID.randomUUID()

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    getOneVolumeUseCase.execute(invalidVolumeId)
                }
            }
        }
    }

    given("워크스페이스가 세팅되지 않고") {

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    getOneVolumeUseCase.execute(targetVolumeId)
                }
            }
        }
    }

    given("올바른 워크스페이스가 세팅되지 않고") {
        beforeContainer {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            val otherWorkspace = WorkspaceGenerator.generateWorkspace(user = targetWorkspace.owner)
            workspaceRepository.save(otherWorkspace.toEntity())
            workspaceInfo.workspace = otherWorkspace
        }

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    getOneVolumeUseCase.execute(targetVolumeId)
                }
            }
        }
    }
})