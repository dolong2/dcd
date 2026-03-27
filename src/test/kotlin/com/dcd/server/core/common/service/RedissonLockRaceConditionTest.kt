package com.dcd.server.core.common.service

import com.dcd.server.core.common.spi.LockPort
import com.dcd.server.infrastructure.global.adapter.RedissonLockServiceAdapter
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
    private val lockService: LockPort
) : BehaviorSpec({

    Given("동시에 여러 스레드가 같은 락을 요청할 때") {

        val threadCount = 10
        val executor = Executors.newFixedThreadPool(threadCount)

        val startLatch = CountDownLatch(1)
        val readyLatch = CountDownLatch(threadCount)
        val endLatch = CountDownLatch(threadCount)

        val counter = AtomicInteger(0)

        When("모든 스레드를 동시에 실행하면") {

            repeat(threadCount) {

                executor.submit {
                    try {
                        readyLatch.countDown()
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

            readyLatch.await(5, java.util.concurrent.TimeUnit.SECONDS) shouldBe true
            startLatch.countDown()
            endLatch.await(5, java.util.concurrent.TimeUnit.SECONDS) shouldBe true

            executor.shutdown()

            Then("block은 단 한 번만 실행된다") {
                counter.get() shouldBe 1
            }
        }
    }
})