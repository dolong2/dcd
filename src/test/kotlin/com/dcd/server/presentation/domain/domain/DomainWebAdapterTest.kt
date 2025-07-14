package com.dcd.server.presentation.domain.domain

import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.core.domain.domain.dto.response.CreateDomainResDto
import com.dcd.server.core.domain.domain.usecase.ConnectDomainUseCase
import com.dcd.server.core.domain.domain.usecase.CreateDomainUseCase
import com.dcd.server.core.domain.domain.usecase.DeleteDomainUseCase
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.util.UUID

class DomainWebAdapterTest : BehaviorSpec({
    val workspaceId= UUID.randomUUID().toString()
    val createDomainUseCase = mockk<CreateDomainUseCase>()
    val deleteDomainUseCase = mockk<DeleteDomainUseCase>(relaxUnitFun = true)
    val connectDomainUseCase = mockk<ConnectDomainUseCase>(relaxUnitFun = true)

    val domainWebAdapter = DomainWebAdapter(
        createDomainUseCase = createDomainUseCase,
        deleteDomainUseCase = deleteDomainUseCase,
        connectDomainUseCase = connectDomainUseCase,
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

    given("도메인 아이디가 주어지고") {
        val domainId = UUID.randomUUID().toString()

        `when`("도메인 삭제 메서드를 실행할때") {
            val result = domainWebAdapter.deleteDomain(UUID.randomUUID().toString(), domainId)

            then("200으로 응답되어야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body shouldBe null
            }
        }
    }
})