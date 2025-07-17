package com.dcd.server.presentation.domain.domain

import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.core.domain.domain.dto.response.CreateDomainResDto
import com.dcd.server.core.domain.domain.dto.response.DomainResDto
import com.dcd.server.core.domain.domain.usecase.*
import com.dcd.server.presentation.domain.domain.data.request.ConnectDomainRequest
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.UUID

class DomainWebAdapterTest : BehaviorSpec({
    val workspaceId= UUID.randomUUID().toString()
    val createDomainUseCase = mockk<CreateDomainUseCase>()
    val deleteDomainUseCase = mockk<DeleteDomainUseCase>(relaxUnitFun = true)
    val connectDomainUseCase = mockk<ConnectDomainUseCase>(relaxUnitFun = true)
    val disconnectDomainUseCase = mockk<DisconnectDomainUseCase>(relaxUnitFun = true)
    val getDomainUseCase = mockk<GetDomainUseCase>()

    val domainWebAdapter = DomainWebAdapter(
        createDomainUseCase = createDomainUseCase,
        deleteDomainUseCase = deleteDomainUseCase,
        connectDomainUseCase = connectDomainUseCase,
        disconnectDomainUseCase = disconnectDomainUseCase,
        getDomainUseCase = getDomainUseCase
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
            val result = domainWebAdapter.deleteDomain(workspaceId, domainId)

            then("200으로 응답되어야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body shouldBe null
            }
        }

        `when`("도메인 연결 메서드를 실행할때") {
            val request = ConnectDomainRequest(UUID.randomUUID().toString())
            val result = domainWebAdapter.connectDomain(workspaceId, domainId, request)

            then("200으로 응답되어야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body shouldBe null
            }
        }

        `when`("도메인 연결 해제 메서드를 실행할때") {
            val result = domainWebAdapter.disconnectDomain(workspaceId, domainId)

            then("200으로 응답되어야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body shouldBe null
            }
        }
    }

    given("아무것도 주어지지 않고") {

        `when`("도메인을 조회할때") {
            val domainResDto = DomainResDto(id = UUID.randomUUID().toString(), name = "testDomain", description = "test", application = null)
            every { getDomainUseCase.execute() } returns ListResDto(listOf(domainResDto))

            val result = domainWebAdapter.getDomainList(workspaceId)

            then("유스케이스에서 반환된 값의 정보를 가지고 있어야함") {
                val list = result.body?.list
                list shouldNotBe null
                list!!.size shouldBe 1

                val domainResponse = list[0]
                domainResponse.id shouldBe domainResDto.id
                domainResponse.name shouldBe domainResDto.name
                domainResponse.description shouldBe domainResDto.description
                domainResponse.application shouldBe domainResDto.application
            }

            then("응답 상태는 OK여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})