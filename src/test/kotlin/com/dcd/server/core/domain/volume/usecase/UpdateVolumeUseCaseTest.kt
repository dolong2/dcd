package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.dto.request.UpdateVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.volume.adapter.toDomain
import com.dcd.server.persistence.volume.adapter.toEntity
import com.dcd.server.persistence.volume.repository.VolumeMountRepository
import com.dcd.server.persistence.volume.repository.VolumeRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import com.dcd.server.persistence.workspace.repository.WorkspaceRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.workspace.WorkspaceGenerator
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UpdateVolumeUseCaseTest(
    private val updateVolumeUseCase: UpdateVolumeUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    private val workspaceInfo: WorkspaceInfo,
    private val volumeRepository: VolumeRepository,
    private val volumeMountRepository: VolumeMountRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
) : BehaviorSpec({
    val targetVolumeId = UUID.randomUUID()

    beforeSpec {
        val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!

        val volume = Volume(
            id = targetVolumeId,
            name = "testVolume",
            description = "testDescription",
            workspace = targetWorkspace
        )
        volumeRepository.save(volume.toEntity())
    }

    given("타겟 볼륨 아이디와 수정할 볼륨 요청 dto가 주어지고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        val request = UpdateVolumeReqDto(name = "updateVolume", description = "updateDescription")

        `when`("유스케이스를 실행할때") {
            updateVolumeUseCase.execute(targetVolumeId, request)

            then("볼륨 정보가 수정되어야함") {
                val targetVolume = volumeRepository.findByIdOrNull(targetVolumeId)

                targetVolume shouldNotBe null
                targetVolume!!.name shouldBe request.name
                targetVolume.description shouldBe request.description
            }
        }
    }

    given("볼륨이 존재하지 않고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        volumeRepository.deleteAll()
        val request = UpdateVolumeReqDto(name = "updateVolume", description = "updateDescription")

        `when`("유스케이스를 실행할때") {

            then("예외가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    updateVolumeUseCase.execute(targetVolumeId, request)
                }
            }
        }
    }

    given("볼륨 마운트가 존재하고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        val application = queryApplicationPort.findById("2fb0f315-8272-422f-8e9f-c4f765c022b2")!!
        val volume = volumeRepository.findByIdOrNull(targetVolumeId)!!.toDomain()
        val volumeMount = VolumeMount(
            id = UUID.randomUUID(),
            application = application,
            volume = volume,
            mountPath = "/test/volume",
            readOnly = false
        )
        volumeMountRepository.save(volumeMount.toEntity())

        val request = UpdateVolumeReqDto(name = "updateVolume", description = "updateDescription")

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<AlreadyExistsVolumeMountException> {
                    updateVolumeUseCase.execute(targetVolumeId, request)
                }
            }
        }
    }

    given("볼륨이 속한 워크스페이스가 아니고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            val otherWorkspace = WorkspaceGenerator.generateWorkspace(user = targetWorkspace.owner)
            workspaceRepository.save(otherWorkspace.toEntity())
            workspaceInfo.workspace = otherWorkspace
        }

        val request = UpdateVolumeReqDto(name = "updateVolume", description = "updateDescription")

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    updateVolumeUseCase.execute(targetVolumeId, request)
                }
            }
        }
    }
})