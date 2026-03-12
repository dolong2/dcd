package com.dcd.server.core.common.service

import com.dcd.server.core.common.service.impl.RedissonLockServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RedissonLockRaceConditionTest(
    private val lockService: LockService
) : BehaviorSpec({

    Given("동시에 여러 스레드가 같은 락을 요청할 때") {

        val threadCount = 10
        val executor = Executors.newFixedThreadPool(threadCount)

        val startLatch = CountDownLatch(1)
        val endLatch = CountDownLatch(threadCount)

        val counter = AtomicInteger(0)

        When("모든 스레드를 동시에 실행하면") {

            repeat(threadCount) {

                executor.submit {
                    try {
                        startLatch.await()

                        lockService.lock("race-lock", 0, 5000) {
                            Thread.sleep(100)
                            counter.incrementAndGet()
                        }

                    } finally {
                        endLatch.countDown()
                    }
                }
            }

            startLatch.countDown()
            endLatch.await()

            executor.shutdown()

            Then("block은 단 한 번만 실행된다") {
                counter.get() shouldBe 1
            }
        }
    }
})