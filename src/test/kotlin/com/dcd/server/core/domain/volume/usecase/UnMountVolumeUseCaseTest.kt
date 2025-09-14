package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.exception.VolumeMountNotFoundException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.workspace.WorkspaceGenerator
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UnMountVolumeUseCaseTest(
    private val unMountVolumeUseCase: UnMountVolumeUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val volumeRepository: VolumeRepository,
    private val volumeMountRepository: VolumeMountRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val targetVolumeId = UUID.randomUUID()
    val targetApplicationId = "2fb0f315-8272-422f-8e9f-c4f765c022b2"

    beforeSpec {
        val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        val volume = Volume(
            id = targetVolumeId,
            name = "test1Volume",
            description = "testDescription",
            workspace = targetWorkspace
        )
        volumeRepository.save(volume.toEntity())

        val application = queryApplicationPort.findById(targetApplicationId)!!
        val volumeMount = VolumeMount(
            id = UUID.randomUUID(),
            application = application,
            volume = volume,
            mountPath = "/test/volume",
            readOnly = false
        )
        volumeMountRepository.save(volumeMount.toEntity())
    }

    given("볼륨과 같은 워크스페이스가 세팅되었고") {
        beforeContainer {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        `when`("유스케이스를 실행할때") {
            unMountVolumeUseCase.execute(targetVolumeId, targetApplicationId)

            then("볼륨 마운트 엔티티가 삭제되어야함") {
                volumeMountRepository.findAll().isEmpty() shouldBe true
            }
        }

        `when`("존재하지 않는 볼륨 아이디로 실행할때") {
            val notFoundVolumeId = UUID.randomUUID()

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    unMountVolumeUseCase.execute(notFoundVolumeId, targetApplicationId)
                }
            }
        }

        `when`("존재하지 않는 애플리케이션 아이디로 실행할때") {
            val notFoundApplicationId = UUID.randomUUID().toString()

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    unMountVolumeUseCase.execute(targetVolumeId, notFoundApplicationId)
                }
            }
        }

        `when`("마운트된 볼륨이 아닐때") {
            beforeContainer {
                volumeMountRepository.deleteAll()
            }

            then("에러가 발생해야함") {
                shouldThrow<VolumeMountNotFoundException> {
                    unMountVolumeUseCase.execute(targetVolumeId, targetApplicationId)
                }
            }
        }
    }

    given("세팅된 워크스페이스가 볼륨이 속한 워크스페이스가 아니고") {
        beforeContainer {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            val otherWorkspace = WorkspaceGenerator.generateWorkspace(user = targetWorkspace.owner)
            workspaceRepository.save(otherWorkspace.toEntity())
            workspaceInfo.workspace = otherWorkspace
        }

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    unMountVolumeUseCase.execute(targetVolumeId, targetApplicationId)
                }
            }
        }
    }

    given("워크스페이스가 세팅되지 않고") {
        beforeContainer {
            workspaceInfo.workspace = null
        }

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    unMountVolumeUseCase.execute(targetVolumeId, targetApplicationId)
                }
            }
        }
    }
})