package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toResDto
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.volume.adapter.toDomain
import com.dcd.server.persistence.volume.adapter.toEntity
import com.dcd.server.persistence.volume.repository.VolumeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetAllVolumeUseCaseTest(
    private val getAllVolumeUseCase: GetAllVolumeUseCase,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val volumeRepository: VolumeRepository,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val targetVolume1Id = UUID.randomUUID()
    val targetVolume2Id = UUID.randomUUID()

    beforeSpec {
        val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        val volume1 = Volume(
            id = targetVolume1Id,
            name = "test1Volume",
            description = "testDescription",
            workspace = targetWorkspace
        ).toEntity()
        val volume2 = Volume(
            id = targetVolume2Id,
            name = "test2Volume",
            description = "testDescription",
            workspace = targetWorkspace
        ).toEntity()
        volumeRepository.saveAll(listOf(volume1, volume2))
    }

    given("알맞는 워크스페이스가 세팅되고") {
        beforeTest {
            val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
            workspaceInfo.workspace = targetWorkspace
        }

        `when`("유스케이스를 실행할때") {
            val result = getAllVolumeUseCase.execute()

            then("타켓 볼륨의 정보가 반환되어야함") {
                val targetVolumeList = volumeRepository.findAllById(listOf(targetVolume1Id, targetVolume2Id))
                targetVolumeList.size shouldBe 2

                result.list shouldContainOnly targetVolumeList.map { it.toDomain().toResDto() }
            }
        }
    }

    given("워크스페이스가 세팅되지 않고") {

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    getAllVolumeUseCase.execute()
                }
            }
        }
    }
})