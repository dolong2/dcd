package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.user.repository.UserRepository
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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.workspace.WorkspaceGenerator
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteVolumeUseCaseTest(
    private val deleteVolumeUseCase: DeleteVolumeUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    private val volumeRepository: VolumeRepository,
    private val volumeMountRepository: VolumeMountRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val userRepository: UserRepository,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo
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

    given("타켓 볼륨 아이디가 주어지고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        `when`("유스케이스를 실행할때") {
            deleteVolumeUseCase.execute(targetVolumeId)

            then("해당 볼륨이 삭제되어야함") {
                volumeRepository.findByIdOrNull(targetVolumeId) shouldBe null
            }
        }
    }

    given("볼륨이 존재하지 않고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        volumeRepository.deleteAll()

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    deleteVolumeUseCase.execute(targetVolumeId)
                }
            }
        }
    }

    given("마운트된 볼륨이 존재하고") {
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

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<AlreadyExistsVolumeMountException> {
                    deleteVolumeUseCase.execute(targetVolumeId)
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

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    deleteVolumeUseCase.execute(targetVolumeId)
                }
            }
        }
    }
})