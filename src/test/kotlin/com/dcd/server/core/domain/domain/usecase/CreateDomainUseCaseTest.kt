package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.core.domain.domain.exception.AlreadyExistsDomainException
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.domain.DomainGenerator

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CreateDomainUseCaseTest(
    private val createDomainUseCase: CreateDomainUseCase,
    private val queryDomainPort: QueryDomainPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandDomainPort: CommandDomainPort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    beforeTest {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")
        workspaceInfo.workspace = workspace
    }

    given("Domain 생성 요청이 주어지고") {
        val testDomainReqDto = CreateDomainReqDto(
            name = "test",
            description = "test",
        )

        `when`("usecase를 실행할때") {
            val response = createDomainUseCase.execute(testDomainReqDto)

            then("응답값의 아이디를 가지는 도메인이 생성되어야함") {
                val domain = queryDomainPort.findById(response.domainId)
                domain shouldNotBe null
                domain?.name shouldBe testDomainReqDto.name
                domain?.description shouldBe testDomainReqDto.description
                domain?.application shouldBe null
                domain?.workspace shouldBe workspaceInfo.workspace
            }
        }

        `when`("이미 같은 이름의 도메인이 존재할때") {
            val alreadyExistsDomain = DomainGenerator.generateDomain(workspace = workspaceInfo.workspace!!)
            commandDomainPort.save(alreadyExistsDomain)

            then("에러가 발생해야함") {
                shouldThrow<AlreadyExistsDomainException> {
                    createDomainUseCase.execute(testDomainReqDto)
                }
            }
        }
    }
})