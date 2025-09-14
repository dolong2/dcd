package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.dto.request.MountVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.workspace.WorkspaceGenerator
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class MountVolumeUseCaseTest(
    private val mountVolumeUseCase: MountVolumeUseCase,
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
        ).toEntity()
        volumeRepository.save(volume)
    }

    given("마운트 요청 객체가 주어지고") {
        beforeContainer {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        val request = MountVolumeReqDto("/test", false)

        `when`("유스케이스를 실행하면") {
            mountVolumeUseCase.execute(targetVolumeId, targetApplicationId, request)

            then("볼륨 마운트가 생성되어야함") {
                val volumeMountList = volumeMountRepository.findAll()
                volumeMountList.size shouldBe 1

                val first = volumeMountList.first()
                first.mountPath shouldBe request.mountPath
                first.readOnly shouldBe request.readOnly
                first.application.id.toString() shouldBe targetApplicationId
                first.volume.id shouldBe targetVolumeId
            }
        }

        `when`("존재하지 않는 볼륨 아이디로 실행하면") {
            val invalidVolumeId = UUID.randomUUID()

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    mountVolumeUseCase.execute(invalidVolumeId, targetApplicationId, request)
                }
            }
        }

        `when`("존재하지 않는 애플리케이션 아이디로 실행하면") {
            val invalidApplicationId = UUID.randomUUID().toString()

            then("에러가 발생행해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    mountVolumeUseCase.execute(targetVolumeId, invalidApplicationId, request)
                }
            }
        }

        `when`("세팅된 워크스페이스가 볼륨이 속한 워크스페이스가 아닐때") {
            beforeTest {
                val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
                val otherWorkspace = WorkspaceGenerator.generateWorkspace(user = targetWorkspace.owner)
                workspaceRepository.save(otherWorkspace.toEntity())
                workspaceInfo.workspace = otherWorkspace
            }

            then("에러가 발생해야함") {
                shouldThrow<VolumeNotFoundException> {
                    mountVolumeUseCase.execute(targetVolumeId, targetApplicationId, request)
                }
            }
        }

        `when`("이미 볼륨에 마운트 됐을때") {
            beforeContainer {
                val application = queryApplicationPort.findById(targetApplicationId)!!
                val volume = volumeRepository.findByIdOrNull(targetVolumeId)!!.toDomain()
                val volumeMount = VolumeMount(
                    application = application,
                    volume = volume,
                    mountPath = "/test/volume",
                    readOnly = false
                )
                volumeMountRepository.save(volumeMount.toEntity())
            }

            then("에러가 발생해야함") {
                shouldThrow<AlreadyExistsVolumeMountException> {
                    mountVolumeUseCase.execute(targetVolumeId, targetApplicationId, request)
                }
            }
        }
    }

    given("올바르지 않은 워크스페이스가 세팅되고") {
        beforeContainer {
            workspaceInfo.workspace = null
        }

        val request = MountVolumeReqDto("/test", false)

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    mountVolumeUseCase.execute(targetVolumeId, targetApplicationId, request)
                }
            }
        }
    }
})