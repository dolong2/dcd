package com.dcd.server.presentation.domain.volume

import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.dto.request.UpdateVolumeReqDto
import com.dcd.server.core.domain.volume.usecase.CreateVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.DeleteVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.UpdateVolumeUseCase
import com.dcd.server.presentation.domain.volume.data.request.CreateVolumeRequest
import com.dcd.server.presentation.domain.volume.data.request.UpdateVolumeRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.UUID

class VolumeWebAdapterTest : BehaviorSpec({
    val createVolumeUseCase = mockk<CreateVolumeUseCase>(relaxUnitFun = true)
    val deleteVolumeUseCase = mockk<DeleteVolumeUseCase>(relaxUnitFun = true)
    val updateVolumeUseCase = mockk<UpdateVolumeUseCase>(relaxUnitFun = true)

    val volumeWebAdapter = VolumeWebAdapter(createVolumeUseCase, deleteVolumeUseCase, updateVolumeUseCase)

    given("워크스페이스 아이디와 볼륨 생성 요청이 주어지고") {
        val testWorkspaceId = UUID.randomUUID().toString()
        val request = CreateVolumeRequest(name = "testVolume", description = "testDescription")

        `when`("볼륨 생성 메서드를 실행하면") {
            val result = volumeWebAdapter.createVolume(testWorkspaceId, request)

            then("상태코드가 OK가 응답되어여함") {
                result.statusCode shouldBe HttpStatus.OK
            }

            then("볼륨 생성 유스케이스를 실행해야함") {
                verify { createVolumeUseCase.execute(any() as CreateVolumeReqDto) }
            }
        }
    }

    given("워크스페이스 아이디와 삭제할 볼륨 아이디가 주어지고") {
        val testWorkspaceId = UUID.randomUUID().toString()
        val testVolumeId = UUID.randomUUID()

        `when`("볼륨 삭제 메서드를 실행하면") {
            val result = volumeWebAdapter.deleteVolume(testWorkspaceId, testVolumeId)

            then("상태코드 OK가 응답되어야함") {
                result.statusCode shouldBe HttpStatus.OK
            }

            then("볼륨 삭제 유스케이스를 실행해야함") {
                verify { deleteVolumeUseCase.execute(testVolumeId) }
            }
        }
    }

    given("워크스페이스 아이디, 볼륨 아이디, 볼륨 수정 요청이 주어지고") {
        val testWorkspaceId = UUID.randomUUID().toString()
        val testVolumeId = UUID.randomUUID()
        val request = UpdateVolumeRequest(name = "testVolume", description = "testDescription")

        `when`("볼륨 수정 메서드를 실행하면") {
            val result = volumeWebAdapter.updateVolume(testWorkspaceId, testVolumeId, request)

            then("상태코드 OK가 응답되어야함") {
                result.statusCode shouldBe HttpStatus.OK
            }

            then("볼륨 수정 유스케이스를 실행해야함") {
                verify { updateVolumeUseCase.execute(testVolumeId, any() as UpdateVolumeReqDto) }
            }
        }
    }
})