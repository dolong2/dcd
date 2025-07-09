package com.dcd.server.presentation.domain.domain

import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.core.domain.domain.dto.response.CreateDomainResDto
import com.dcd.server.core.domain.domain.usecase.CreateDomainUseCase
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class DomainWebAdapterTest : BehaviorSpec({
    val workspaceId= UUID.randomUUID().toString()
    val createDomainUseCase = mockk<CreateDomainUseCase>()

    val domainWebAdapter = DomainWebAdapter(
        createDomainUseCase = createDomainUseCase,
    )

    given("CreateDomainRequest가 주어지고") {
        val request = CreateDomainRequest(
            name = "domainName",
            description = "domainDescription"
        )

        `when`("도메인 생성 메서드를 실행하면") {
            val domainId = UUID.randomUUID().toString()
            every { createDomainUseCase.execute(any() as CreateDomainReqDto) } returns CreateDomainResDto(domainId)

            val response = domainWebAdapter.createDomain(workspaceId, request)
            then("유스케이스에서 반환한 도메인 아이디를 본문에 담아야함") {
                response.body?.domainId shouldBe domainId
            }
        }
    }
})