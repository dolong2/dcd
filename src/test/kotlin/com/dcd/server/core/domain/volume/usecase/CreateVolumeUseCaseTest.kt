package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toEntity
import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.user.repository.UserRepository
import com.dcd.server.persistence.volume.adapter.toEntity
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
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CreateVolumeUseCaseTest(
    private val createVolumeUseCase: CreateVolumeUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    private val volumeRepository: VolumeRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val userRepository: UserRepository,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({

    given("목표 워크스페이스와 볼륨 생성 요청이 주어지고") {
        val targetWorkspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        workspaceInfo.workspace = targetWorkspace
        val createVolumeReqDto = CreateVolumeReqDto(name = "testVolume", description = "test")

        `when`("목표 워크스페이스에 중복되는 이름의 볼륨이 없을때") {
            createVolumeUseCase.execute(createVolumeReqDto)

            then("요청 정보를 가진 볼륨이 정상적으로 생성되어야함") {
                val volumeList = volumeRepository.findAll()
                volumeList.size shouldBe 1

                val targetVolume = volumeList.first()
                targetVolume.name shouldBe createVolumeReqDto.name
                targetVolume.description shouldBe createVolumeReqDto.description
                targetVolume.workspace.id.toString() shouldBe workspaceInfo.workspace!!.id
            }
        }

        volumeRepository.deleteAll()

        `when`("다른 워크스페이스에 해당 이름의 볼륨이 존재할때") {
            val user = UserGenerator.generateUser()
            userRepository.save(user.toEntity())
            val workspace = WorkspaceGenerator.generateWorkspace(user = user)
            workspaceRepository.save(workspace.toEntity())
            val volumeModel = createVolumeReqDto.toEntity(workspace)
            volumeRepository.save(volumeModel.toEntity())

            createVolumeUseCase.execute(createVolumeReqDto)

            then("볼륨은 정상적으로 생성되어야함") {
                val volumeList = volumeRepository.findAll().filter { it.workspace.id.toString() == workspaceInfo.workspace!!.id }
                volumeList.size shouldBe 1

                val targetVolume = volumeList.first()
                targetVolume.name shouldBe createVolumeReqDto.name
                targetVolume.description shouldBe createVolumeReqDto.description
                targetVolume.workspace.id.toString() shouldBe workspaceInfo.workspace!!.id
            }
        }

        volumeRepository.deleteAll()

        `when`("이미 해당 이름의 볼륨이 존재할때") {
            val volumeModel = createVolumeReqDto.toEntity(workspaceInfo.workspace!!)
            volumeRepository.save(volumeModel.toEntity())

            then("볼륨 생성에 실패해야함") {
                shouldThrow<AlreadyExistsVolumeException> {
                    createVolumeUseCase.execute(createVolumeReqDto)
                }
            }
        }
    }

    given("워크스페이스 정보가 초기화되지 않고") {
        workspaceInfo.workspace = null
        val createVolumeReqDto = CreateVolumeReqDto(name = "testVolume", description = "test")

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    createVolumeUseCase.execute(createVolumeReqDto)
                }
            }
        }
    }
})