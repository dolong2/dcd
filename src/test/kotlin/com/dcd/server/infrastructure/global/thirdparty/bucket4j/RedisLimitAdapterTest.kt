package com.dcd.server.infrastructure.global.thirdparty.bucket4j

import com.dcd.server.core.common.annotation.Limit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RedisLimitAdapterTest(
    private val redisLimitAdapter: RedisLimitAdapter
) : BehaviorSpec({
    given("테스트할 Limit 어노테이션이 주어지고") {
        val targetLimitAnnotation = Limit(target = "test")

        `when`("어노테이션에 설정된 횟수 초과로 토큰을 소비할때") {
            val capacity = targetLimitAnnotation.capacity
            for (i in 0 until capacity) {
                redisLimitAdapter.consumer(targetLimitAnnotation.target, targetLimitAnnotation)
            }

            then("반환값이 false여야함") {
                redisLimitAdapter.consumer(targetLimitAnnotation.target, targetLimitAnnotation) shouldBe false
            }
        }
    }
})