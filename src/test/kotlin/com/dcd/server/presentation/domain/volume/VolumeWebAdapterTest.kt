package com.dcd.server.presentation.domain.volume

import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.usecase.CreateVolumeUseCase
import com.dcd.server.presentation.domain.volume.data.request.CreateVolumeRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.UUID

class VolumeWebAdapterTest : BehaviorSpec({
    val createVolumeUseCase = mockk<CreateVolumeUseCase>(relaxUnitFun = true)

    val volumeWebAdapter = VolumeWebAdapter(createVolumeUseCase)

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
})