package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.GetApplicationVersionServiceImpl
import com.dcd.server.core.domain.application.spi.ImageVersionPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetApplicationVersionServiceImplTest : BehaviorSpec({

    val imageVersionPort = mockk<ImageVersionPort>()
    val service = GetApplicationVersionServiceImpl(imageVersionPort)

    given("Docker 태그 목록이 주어졌을 때") {

        every { imageVersionPort.fetchAllVersions("openjdk") } returns listOf(
            "8",
            "11",
            "12",
            "12.0.1",
            "17",
            "17.0.2",
            "invalid",
            "latest",
            "17.0.2" // duplicate
        )

        `when`("SPRING_BOOT 버전을 조회하면") {

            val result = service.getAvailableVersion(ApplicationType.SPRING_BOOT)

            then("최소 버전(12) 초과의 semver만 정렬 후 반환된다") {
                result shouldBe listOf(
                    "17.0.2",
                    "17",
                    "12.0.1"
                )
            }

            then("올바른 이미지명이 Port에 전달된다") {
                verify(exactly = 1) {
                    imageVersionPort.fetchAllVersions("openjdk")
                }
            }
        }
    }

    given("유효하지 않은 태그만 존재할 때") {

        every { imageVersionPort.fetchAllVersions("redis") } returns listOf(
            "latest",
            "alpine",
            "foo"
        )

        `when`("REDIS 버전을 조회하면") {
            val result = service.getAvailableVersion(ApplicationType.REDIS)

            then("빈 리스트가 반환된다") {
                result shouldBe emptyList()
            }
        }
    }
})